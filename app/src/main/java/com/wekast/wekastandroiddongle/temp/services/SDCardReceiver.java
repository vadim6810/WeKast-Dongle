package com.wekast.wekastandroiddongle.temp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SDCardReceiver extends BroadcastReceiver {

    private static String TAG = "wekastdongle";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("TAG", "SDCardReceiver android.intent.action.MEDIA_MOUNTED cached");
        if ("android.intent.action.MEDIA_MOUNTED".equals(intent.getAction())) {
            Log.d("TAG", "android.intent.action.MEDIA_MOUNTED cached");
        }
    }
}
