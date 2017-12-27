package fr.mistercraft.dedsec.ctosclient;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class BouttonActivity extends AppCompatActivity {
    private View menuaff;
    private Boolean menuoppen = false;
    private SocketService socketservice;
    boolean mBound = false;
    private String onScreenBouttonCode = "-none-";
    private View mainbutton;
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

    protected void onStart() {
        super.onStart();
        Intent serviceintent = new Intent(this, SocketService.class);
        bindService(serviceintent, serviceconnection, Context.BIND_AUTO_CREATE);
    }
/*
    protected void onDestroy() {
        super.onDestroy();
        try {
            socketservice.socket.close();
        } catch (IOException e) {
            Log.e("E", "IO ", e);
        }
        unbindService(serviceconnection);
        mBound = false;
    }*/

    public void disconnectSocket() {
        try {
            try {
                socketservice.socket.close();
            } catch (IOException e) {
                Log.e("IO", "", e);
            }
            unbindService(serviceconnection);
            mBound = false;
        } catch (Exception e) {
            Log.i("Socket", "No socket connected");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutton);
        menuaff = findViewById(R.id.MenuIncluder);
        mainbutton = findViewById(R.id.MainButton);
    }

    public void actionChoice(View view) {
        onScreenBouttonCode = view.getContentDescription().toString();
        mainbutton.setForeground(view.getForeground());
        Log.i("Choice", "Code = " + onScreenBouttonCode);
        Log.i("Choice", "Drawable = " + view.getForeground().toString());
        menuoppenner(null);
    }

    public void onMainBouttonClick(View view) {
        Log.i("Button activity", "Main button clicked");
        if (!menuoppen) {
            if (onScreenBouttonCode.substring(0, 5).equals("basic")) {
                socketservice.send(onScreenBouttonCode.substring(5));
            } else {
                if (onScreenBouttonCode.substring(0, 5).equals("code1")) {
                    //Something that need optional complex interactions
                } else if (onScreenBouttonCode.substring(0, 5).equals("code2")) {
                    //Something else that need optional input for exemple
                } else {
                    socketservice.send("yolo");
                }
            }
        } else {
            menuoppenner(null);
        }
    }

    public void menuoppenner(View view) {
        if (!menuoppen) {
            menuaff.setVisibility(View.VISIBLE);
            menuoppen = true;
            Log.i("Button activity", "Opening menu");
        } else {
            menuaff.setVisibility(View.GONE);
            menuoppen = false;
            Log.i("Button activity", "Closing menu");
        }
    }
}
