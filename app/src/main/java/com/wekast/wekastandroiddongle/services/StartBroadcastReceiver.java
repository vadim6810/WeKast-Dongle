package com.wekast.wekastandroiddongle.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wekast.wekastandroiddongle.Utils.Loger;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class StartBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = "wekastdongle";
    private Loger log = Loger.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        log.createLogger("StartBroadcastReceiver onReceive()");
        System.out.println("BOOT RECEIVED");
        Log.d(TAG, "StartBroadcastReceiver onReceive()");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, DongleServiceOld.class);
            context.startService(pushIntent);
            Log.d(TAG, "StartBroadcastReceiver onReceive() DongleServiceOld started");
            System.out.println("BOOT RECEIVED dongleService started");

//            pushIntent = new Intent();
//            pushIntent.setClass(context, MainActivity.class);
//            pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(pushIntent);

//            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("com.wekast.wekastandroiddongle");
//            context.startActivity(LaunchIntent);
        }
    }
}
