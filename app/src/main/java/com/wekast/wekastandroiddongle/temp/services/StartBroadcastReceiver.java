package com.wekast.wekastandroiddongle.temp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wekast.wekastandroiddongle.Utils.Loger;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class StartBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // Все равно автостарт выполняется уже после загрузки Лаунчера, но на всякий случай можно
            Log.i("Dongle", "Auto start");
//            Intent pushIntent = new Intent(context, DongleService.class);
//            context.startService(pushIntent);
//            Log.d(TAG, "StartBroadcastReceiver onReceive() DongleServiceOld started");
//            System.out.println("BOOT RECEIVED dongleService started");

            Intent pushIntent = new Intent(context, FullscreenActivity.class);
            pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(pushIntent);

//            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("com.wekast.wekastandroiddongle");
//            context.startActivity(LaunchIntent);
        }
    }
}
