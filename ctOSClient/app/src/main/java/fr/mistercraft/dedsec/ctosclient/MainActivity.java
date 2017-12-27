package fr.mistercraft.dedsec.ctosclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    protected static final String SavedIP_location = "fr.mistercraft.dedsec.ctosclient.IP";
    private SocketService socketservice;
    boolean mBound = false;
    protected static boolean connected = false;
    protected final Context context = this;
    private String ip;
    private Boolean connecting = false;

    protected ServiceConnection serviceconnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
            socketservice = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences SavedIP = this.getSharedPreferences(SavedIP_location, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textview = findViewById(R.id.IP);
        String savedIP = SavedIP.getString("IP", null);
        textview.setText(savedIP);
        setTitle(R.string.connection);
    }

    protected void onStart() {
        super.onStart();
        Intent serviceintent = new Intent(this, SocketService.class);
        bindService(serviceintent, serviceconnection, Context.BIND_AUTO_CREATE);
    }

    protected void onResume() {
        super.onResume();
        View progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        BouttonActivity a = new BouttonActivity();
        a.disconnectSocket();
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceconnection);
        mBound = false;
    }

    public void saveIP (View view) {
        EditText IP = findViewById(R.id.IP);
        ip = IP.getText().toString();
        SharedPreferences SavedIP = this.getSharedPreferences(SavedIP_location, MODE_PRIVATE);
        SharedPreferences.Editor saveIP = SavedIP.edit();
        saveIP.putString("IP", ip);
        saveIP.apply();
        try
        {
            if(!connecting) {
                connectThread connectThread = new connectThread();
                View progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                //TODO : Make loading circle INVISIBLE when connection fail
                connectThread.start();
            }else{
                Toast.makeText(context, R.string.connecting, Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {Toast.makeText(this, "Erreur "+e, Toast.LENGTH_SHORT).show();Log.e("e","Erreur", e);}
    }
    class connectThread extends Thread {
        public void run() {
            connecting = true;
            socketservice.socketConnect(ip);
            connecting = false;
        }
    }
}