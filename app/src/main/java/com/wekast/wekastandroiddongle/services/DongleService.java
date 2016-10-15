package com.wekast.wekastandroiddongle.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class DongleService extends Service {

    public static final String TAG = "Dongole";

    public DongleService() {
        Log.i(TAG, "Service started");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.i(TAG, "Thread is main, create new");
        } else {
            Log.i(TAG, "Service is not main");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
