package com.wekast.wekastandroiddongle.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.wekast.wekastandroiddongle.activity.FullscreenActivity;

public class WifiConnected extends BroadcastReceiver {

    private static final String TAG = "WifiConnected";
    private static final Handler handler = null;

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

//        if (wifiState == 0) {
//            Log.e(TAG, "0 - WIFI_STATE_DISABLING");
//        }
//        if (wifiState == 1) {
//            Log.e(TAG, "1 - WIFI_STATE_DISABLED");
//        }
//        if (wifiState == 2) {
//            Log.e(TAG, "2 - WIFI_STATE_ENABLING");
//        }
//        if (wifiState == 3) {
//            Log.e(TAG, "3 - WIFI_STATE_ENABLED");
//        }
//        if (wifiState == 4) {
//            Log.e(TAG, "4 - WIFI_STATE_UNKNOWN");
//        }


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        boolean isConnected = (netInfo != null && netInfo.isConnected());
        Log.e(TAG, "isConnected: " + isConnected);


        if (isConnected) {

            Intent newIntent = new Intent("MAIN_WINDOW");
            newIntent.putExtra("command", "info");
            newIntent.putExtra("message", "DONGLE CONNECTED\nWAITING PRESENTATION\n\n");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } else {
            Intent newIntent = new Intent("MAIN_WINDOW");
            newIntent.putExtra("command", "info");
            newIntent.putExtra("message", "DONGLE SEARCHING FOR CLIENT...\n\n");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        // TODO: check wifi connected and ssid
//        context.startService(new Intent(context, IsWiFiConnectedService.class));
//        throw new UnsupportedOperationException("Not yet implemented");
    }

}
