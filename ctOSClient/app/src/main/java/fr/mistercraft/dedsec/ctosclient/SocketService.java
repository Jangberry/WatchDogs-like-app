package fr.mistercraft.dedsec.ctosclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketService extends Service {
    Socket socket;
    BufferedReader in;
    OutputStream out;
    private final IBinder mBinder = new LocalBinder();

    protected class LocalBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    protected class SendThread implements Runnable {
        private String message;

        public SendThread(String message) {
            this.message = message;
        }

        public void run() {
            try {
                out.write(message.getBytes());
                Log.i("Sending", "Sent : " + message);
            } catch (IOException e) {
                Log.e("IOException", "", e);
                Intent intent = new Intent(SocketService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("E", "Unknown error ", e);
                Intent intent = new Intent(SocketService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    public void send(String message) {
        SendThread sending = new SendThread(message);
        new Thread(sending).start();
    }

    public String recv() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return "Read failed";
        }
    }
    // TODO : Have a recv method
    public void SocketConnect(String ip) {
        try {
            SocketAddress socaddrs = new InetSocketAddress(ip, 4277);
            socket = new Socket();
            Log.i("SocketService", "Tying to connect");
            socket.connect(socaddrs, 5000);
            if (socket.isConnected()) {
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = socket.getOutputStream();
                Intent intent = new Intent(SocketService.this, BouttonActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Log.i("SocketService", "Socket connected");
            }
        } catch (IOException e) {
            Log.e("e", "IOErreur", e);
        } catch (Exception e) {
            Log.e("e", "Erreur ", e);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("e", "IO", e);
        }
        Log.i("SocketService", "Socket closed");
    }
}