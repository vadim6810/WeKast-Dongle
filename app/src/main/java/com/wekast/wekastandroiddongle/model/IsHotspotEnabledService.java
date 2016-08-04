package com.wekast.wekastandroiddongle.model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class IsHotspotEnabledService extends Service {
    private static final String TAG = "wekastdongle";
    boolean isAPEnabled = false;
    IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public IsHotspotEnabledService getServerInstance() {
            return IsHotspotEnabledService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
//        someTask();
//        return super.onStartCommand(intent, flags, startId);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                someTask();
            }
        });
        t.start();
        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void someTask() {
//        WifiManager wifiManager = (WifiManager) this.getSystemService(this.WIFI_SERVICE);
//        WifiInfo info = wifiManager.getConnectionInfo();

//        WifiManager wifi = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        final List<ScanResult> results = wifiManager.getScanResults();
        if (results != null) {
//            StringBuffer buf = new StringBuffer();
            isAPEnabled = false;
            for(int i = 0; i < results.size(); i++) {
                String ssid = results.get(i).SSID;
                if (ssid.equals("wekast")) {
                    isAPEnabled = true;
                    Log.d(TAG, "IsHotspotEnabledService.someTask() isAPEnabled true");
//                    buf.append(ssid + );
                }
            }
        }


        //job completed. Rest for 30 second before doing another one
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //do job again
        someTask();
    }

//    public boolean getIsAPEnabled() {
//        return isAPEnabled;
//    }
}
