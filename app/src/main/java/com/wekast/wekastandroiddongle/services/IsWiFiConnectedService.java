package com.wekast.wekastandroiddongle.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.wekast.wekastandroiddongle.controllers.WifiController;

public class IsWiFiConnectedService extends Service {

    private static final String TAG = "IsWiFiConnectedService";

    class IsWiFiConnectedServiceThread extends Thread {

        IsWiFiConnectedServiceThread() {
            setDaemon(true);
            setName("IsWiFiConnectedServiceThread");
        }

        @Override
        public void run() {
            wifiController = new WifiController(getApplicationContext());

            boolean lostWifi = false;
            while (!lostWifi) {
                if (wifiController.isWifiConnected()) {
                    timeStart = 0;
                } else {
                    if(isPeriodEnded()) {
                        lostWifi = true;
                        disableWifi();
//                        wifiController.logToTextView("Wifi module disabled", "true");
                        Log.e(TAG, "Wifi module disabled: true");
                        enableAp();
                        restoreDesktop();
                        break;
                    }
                }
                waitForTenSeconds();
            }

            onDestroy();
        }
    }

    private WifiController wifiController;
    private long timeStart = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service isWifiConnnected started");
        Thread.currentThread().setName("IsWifiConnectedService");

        IsWiFiConnectedServiceThread isWiFiConnectedServiceThread = new IsWiFiConnectedServiceThread();
        isWiFiConnectedServiceThread.start();
    }

    @Override
    public void onDestroy() {
        this.stopSelf();
        Log.e(TAG, "Service isWifiConnnected stopped");
        super.onDestroy();
    }

//    private boolean checkIsWifiConnected() {
//        boolean connected = wifiController.isWifiConnected();

//        Log.i(TAG, "Is wifi connected: " + connected);
//        if (connected) {
//            timeStart = 0;
//            waitForTenSeconds();
//            checkIsWifiConnected();
//        }
//
//        if(!isPeriodEnded()) {
//            waitForTenSeconds();
//            checkIsWifiConnected();
//        }
//
//        disableWifi();
//        enableAp();

//        return connected;
//    }

    private void disableWifi() {
        wifiController.disableWifi();
    }

    private void enableAp() {
        wifiController.startAP();
    }

    private void restoreDesktop() {
        wifiController.restoreDesktop();
    }

    private void waitForTenSeconds() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isPeriodEnded() {
        if (timeStart == 0) {
            timeStart = System.currentTimeMillis();
        } else {
            if ((System.currentTimeMillis() - timeStart) > 30000) {
                return true;
            }
        }
        return false;
    }

}
