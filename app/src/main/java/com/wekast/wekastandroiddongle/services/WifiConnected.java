package com.wekast.wekastandroiddongle.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiConnected extends BroadcastReceiver {

    public WifiConnected() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
//        <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
//        <action android:name="android.net.wifi.STATE_CHANGE" />

//        0 - WIFI_STATE_DISABLING
//        1 - WIFI_STATE_DISABLED
//        2 - WIFI_STATE_ENABLING
//        3 - WIFI_STATE_ENABLED
//        4 - WIFI_STATE_UNKNOWN

        String action = intent.getAction();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState();

//        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

        if (wifiState == 3) {

        }

        // TODO: check wifi connected and ssid
//        context.startService(new Intent(context, IsWiFiConnectedService.class));
//        throw new UnsupportedOperationException("Not yet implemented");
    }

}
