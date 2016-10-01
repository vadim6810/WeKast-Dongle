package com.wekast.wekastandroiddongle.activity;

import android.app.ActivityManager;
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
import com.wekast.wekastandroiddongle.Utils.Loger;
import com.wekast.wekastandroiddongle.controllers.ControllerAccessPoint;
import com.wekast.wekastandroiddongle.controllers.ControllerWifi;
import com.wekast.wekastandroiddongle.services.DongleService;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.models.DongleAccessPoint;

/**
 * Explanations
 *
 *
 * Created by YEHUDA on 8/1/2016.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "wekastdongle";
    private Loger log = Loger.getInstance();
    private Context context = this;
    private DongleService dongleService ;
    private boolean isBound = false;

    private WifiManager wifiManager = null;
    private ControllerWifi wifiController = null;
    private ControllerAccessPoint accessPointController = null;
    private DongleAccessPoint dongleAccessPoint = null;


//    private ServiceConnection serviceConnection;
//    private void bindDongleService() {
//        serviceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                // cast the IBinder and get MyService instance
//                DongleService.MyBinder binder = (DongleService.MyBinder) service;
//                dongleService = binder.getService();
//                dongleService.setActivity(MainActivity.this);
//                isBound = true;
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                dongleService = null;
//                isBound = false;
//            }
//        };
//    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to DongleService, cast the IBinder and get DongleService instance
            DongleService.DongleServiceBinder binder = (DongleService.DongleServiceBinder) service;
            dongleService = binder.getService();
            dongleService.setActivity(MainActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

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

        ///////////////////////////////////////////////////////////////
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        ///////////////////////////////////////////////////////////////

        setContentView(R.layout.activity_main);


        // add permission - android.permission.WAKE_LOCK
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "server");
//        wakeLock.acquire();

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiController = new ControllerWifi(wifiManager);
        accessPointController = new ControllerAccessPoint(wifiManager);



//        log.createLogger("Before setting application path");
        log.setAppPath(context.getApplicationInfo().dataDir);

        // TODO: save wifi adapter state, access point state to shared preferences
        saveWifiAdapterState();

        // without not started on dongle own access point at startup application
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create and start Access Point
        initializeWifiAccessPoint();

//        workWithZip();

        Log.d(TAG, "MainActivity.onCreate()");
        log.createLogger("MainActivity.onCreate()");
    }

    /*private void workWithZip() {
        EZSFile ezsFile = new EZSFile();
        ZipFile zipFile = null;
        File file = null;
        try {

            zipFile = new ZipFile("/sdcard/wekast/flip_split2.ezs");

            ZipInputStream zipinputstream = null;
            ZipEntry zipentry = null;
            zipinputstream = new ZipInputStream(new FileInputStream("/sdcard/wekast/flip_split2.ezs"));

            zipentry = zipinputstream.getNextEntry();

            while (zipentry != null) {
                //            int n;
                //            FileOutputStream fileoutputstream;
                //            File newFile = new File(destination, zipentry.getName());
                File newFile = new File(zipentry.getName());
                if (zipentry.isDirectory() && zipentry.getName().equals("animations")) {
//                    newFile.mkdirs();
                    zipentry = zipinputstream.getNextEntry();
                    while (zipentry != null) {
                        final InputStream zipStream = zipFile.getInputStream(zipentry);
                        InputSupplier<InputStream> supplier = new InputSupplier<InputStream>() {
                            InputStream getInput() {
                                return zipStream;
                            }
                        };
                        MediaStore.Files.copy(supplier, unzippedEntryFile);
                    }
                }
                //
                //            if (newFile.exists() && overwrite) {
                //                log.info("Overwriting " + newFile);
                //                newFile.delete();
                //            }
                //
                //            fileoutputstream = new FileOutputStream(newFile);
                //
                //            while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
                //                fileoutputstream.write(buf, 0, n);
                //            }
                //
                //            fileoutputstream.close();
                //            zipinputstream.closeEntry();
                //            zipentry = zipinputstream.getNextEntry();
            }
            zipinputstream.close();
        } catch (Exception e) {
            throw new IllegalStateException("Can't unzip input stream", e);
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();

        // Bind to DongleService
        Intent intent = new Intent(this, DongleService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Log.d(TAG, "MainActivity.onStart()");
        log.createLogger("MainActivity.onStart()");

        // TODO: remove, exist in StartBroadcastReceiver
        Boolean isDongleServiceRunning = isServiceRunning();
        if (!isDongleServiceRunning) {
//            Intent pushIntent = new Intent(this, DongleService.class);
            Intent pushIntent = new Intent(context, DongleService.class);
            startService(pushIntent);
        }
        isDongleServiceRunning = isServiceRunning();
        Log.d(TAG, "MainActivity.onCreate() Starting DongleService: " + isDongleServiceRunning);
        log.createLogger("MainActivity.onCreate() Starting DongleService: " + isDongleServiceRunning);


        // Bind DongleService
//        Intent mIntent = new Intent(this, DongleService.class);
//        bindDongleService();
//        bindService(mIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity.onPause()");
        log.createLogger("MainActivity.onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity.onResume()");
        log.createLogger("MainActivity.onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopService(new Intent(this, DongleService.class));

        // Unbind from the DongleService
        if (isBound) {
            unbindService(mConnection);
            isBound = false;
        }

        // unbind DongleService
//        if (isBound) {
//            unbindService(serviceConnection);
//            isBound = false;
//        }
        Log.d(TAG, "MainActivity.onStop()");
        log.createLogger("MainActivity.onStop()");
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
//        dongleAccessPoint.destroyAccessPoint();
//        wifiController.turnOnOffWifi(this, false);
        // TODO: check if work
//        stopService(new Intent(this, DongleService.class));
//        this.finish();
        Log.d(TAG, "MainActivity.onDestroy()");
        log.createLogger("MainActivity.onDestroy()");
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
     * Creating default
     * Access Point
     */
    private void initializeWifiAccessPoint (){
        dongleAccessPoint = new DongleAccessPoint(this);
        dongleAccessPoint.createAccessPoint();
        Utils.toastShowBottom(this, "Default Access Point started");
        Log.d(TAG, "MainActivity.initializeWifiAccessPoint() ");
        log.createLogger("MainActivity.initializeWifiAccessPoint()");
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.wekast.wekastandroiddongle.services.DongleService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
