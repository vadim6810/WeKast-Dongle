package com.wekast.wekastandroiddongle.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class StartBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = "wekastdongle";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "StartBroadcastReceiver onReceive()");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, DongleService.class);
            context.startService(pushIntent);
        }
    }
}
