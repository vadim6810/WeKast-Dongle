package com.wekast.wekastandroiddongle.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.wekast.wekastandroiddongle.controllers.CommandController;
import com.wekast.wekastandroiddongle.controllers.SocketController;
import com.wekast.wekastandroiddongle.controllers.WifiController;

import java.io.IOException;

public class DongleService extends Service {

    private ServiceThread thread;

    private WifiController wifiController;
    private SocketController socketController;
    private CommandController commandController;

    public WifiController getWifiController() {
        return wifiController;
    }

    public SocketController getSocketController() {
        return socketController;
    }

    public CommandController getCommandController() {
        return commandController;
    }

    public void showSlide(String slide, String animation) {
        Intent intent = new Intent("INTENT_FILTER_NAME");
        intent.putExtra("command", "slide");
        intent.putExtra("slide", slide);
        intent.putExtra("animation", animation);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public void stopPresentation() {
        Intent intent = new Intent("INTENT_FILTER_NAME");
        intent.putExtra("command", "stop");
        intent.putExtra("stop", "1");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    class ServiceThread extends Thread {

        ServiceThread() {
            setDaemon(true);
            setName("DongleServiceThread");
        }

        @Override
        public void run() {
            init();
        }
    }

    private void init() {
        if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_OFF) {
            wifiController.changeState(WifiController.WifiState.WIFI_STATE_AP);
            socketController.waitForTask();
        } else if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_CONNECT) {
        } else if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_AP) {
        }
    }

    public static final String TAG = "Dongle";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service started");
        super.onCreate();

        try {
            wifiController = new WifiController(getApplicationContext());
            commandController = new CommandController(this);
            socketController = new SocketController(commandController);
            thread = new ServiceThread();
            thread.start();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        try {
            Log.i(TAG, "Service stopped");
            socketController.close();
            wifiController.restore();
            if (thread != null) {
                thread.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            super.onDestroy();
        }
    }



}
