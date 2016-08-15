package com.wekast.wekastandroiddongle.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class DongleBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "wekastdongle";

    ControllerWifi wifiController;

    public DongleBroadcastReceiver() {
        Log.d(TAG, "DongleBroadcastReceiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "DongleBroadcastReceiver onReceive()");

        int curState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

        if(curState == WifiManager.WIFI_STATE_ENABLED) {
            Toast.makeText(context.getApplicationContext(), "WIFI ENABLED", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "DongleBroadcastReceiver.onReceive WIFI_STATE_ENABLED");
        } else if(curState == WifiManager.WIFI_STATE_DISABLED) {
            Toast.makeText(context.getApplicationContext(), "WIFI DISABLED", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "DongleBroadcastReceiver.onReceive WIFI_STATE_DISABLED");
        }

    }
}
