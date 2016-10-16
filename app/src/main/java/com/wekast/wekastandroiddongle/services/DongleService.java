package com.wekast.wekastandroiddongle.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
        if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_NONE) {
            boolean result = wifiController.startAP();
            if (result) {
                socketController.waitForTask();
            }
        } else if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_CONNECTED) {
            wifiController.startConnection();
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
            if (thread != null) {
                thread.interrupt();
            }
            socketController.close();
            wifiController.restore();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            super.onDestroy();
        }
    }
}
