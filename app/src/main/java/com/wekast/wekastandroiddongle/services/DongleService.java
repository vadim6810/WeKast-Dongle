package com.wekast.wekastandroiddongle.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.wekast.wekastandroiddongle.controllers.SocketController;
import com.wekast.wekastandroiddongle.controllers.WifiController;

public class DongleService extends Service {

    private ServiceThread thread;

    class ServiceThread extends Thread {

        ServiceThread() {
            setDaemon(true);
            setName("DongleServiceThread");
        }

        @Override
        public void run() {

            WifiController wifiController = new WifiController(getApplicationContext());
            SocketController socketController = new SocketController();
            if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_NONE) {
                boolean result = wifiController.startAP();
                if (result) {
                    socketController.waitForTask();
                }
            } else if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_CONNECTED) {
                wifiController.startConnection();
            }
        }
    }

    public static final String TAG = "Dongle";

    public DongleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service started");

        thread = new ServiceThread();
        thread.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: interrupt
        thread.stop();
    }
}
