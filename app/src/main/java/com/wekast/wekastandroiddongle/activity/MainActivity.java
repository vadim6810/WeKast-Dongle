package com.wekast.wekastandroiddongle.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.controllers.ControllerAccessPoint;
import com.wekast.wekastandroiddongle.controllers.ControllerWifi;
import com.wekast.wekastandroiddongle.services.DongleService;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.models.DongleAccessPoint;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "wekastdongle";
    private Context context = this;
    private DongleService dongleService;
    private boolean isBound = false;

    private WifiManager wifiManager = null;
    private ControllerWifi wifiController = null;
    private ControllerAccessPoint accessPointController = null;
    private DongleAccessPoint dongleAccessPoint = null;


    private ServiceConnection serviceConnection;
    private void bindDongleService() {

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // cast the IBinder and get MyService instance
                DongleService.MyBinder binder = (DongleService.MyBinder) service;
                dongleService = binder.getService();
                dongleService.setActivity(MainActivity.this);
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                dongleService = null;
                isBound = false;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        // hide action bar
        getSupportActionBar().hide();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);

        // TODO: remove, exist in StartBroadcastReceiver
        Intent pushIntent = new Intent(context, DongleService.class);
        context.startService(pushIntent);

        // add permission - android.permission.WAKE_LOCK
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "server");
//        wakeLock.acquire();

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiController = new ControllerWifi(wifiManager);
        accessPointController = new ControllerAccessPoint(wifiManager);

        // TODO: save wifi adapter state, access point state to shared preferences
        saveWifiAdapterState();

        // Create and start Access Point
        initializeWifiAccessPoint();

        Log.d(TAG, "MainActivity.onCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity.onStart()");

        // Bind DongleService
        Intent mIntent = new Intent(this, DongleService.class);
        bindDongleService();
        bindService(mIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity.onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity.onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity.onStop()");
        // unbind DongleService
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dongleAccessPoint.destroyAccessPoint();
        wifiController.turnOnOffWifi(this, false);
        // TODO: check if work
        stopService(new Intent(this, DongleService.class));
        this.finish();
        Log.d(TAG, "MainActivity.onDestroy()");
    }

    private void saveWifiAdapterState() {
        // TODO: Do we need this stuff on Dongle?
        Boolean isWifiEnabled = wifiController.isWifiOn(MainActivity.this);
        // TODO: save access point state to isAccessPointEnabled
        if(isWifiEnabled) {
            Utils.setFieldSP(MainActivity.this, "WIFI_STATE_BEFORE_LAUNCH_APP", isWifiEnabled.toString());
            Utils.setFieldSP(MainActivity.this, "ACCESS_POINT_STATE_BEFORE_LAUNCH_APP", "false");
            // TODO: save connected wifi ssid
        } else {
            Utils.setFieldSP(MainActivity.this, "WIFI_STATE_BEFORE_LAUNCH_APP", isWifiEnabled.toString());
            Utils.setFieldSP(MainActivity.this, "ACCESS_POINT_STATE_BEFORE_LAUNCH_APP", "false");
        }
    }

    /**
     * Creating default Access Point
     */
    private void initializeWifiAccessPoint (){
        dongleAccessPoint = new DongleAccessPoint(this);
        dongleAccessPoint.createAccessPoint();
        Utils.toastShowBottom(this, "Default Access Point started");
        Log.d(TAG, "MainActivity.initializeWifiAccessPoint() ");
    }

}
