package com.wekast.wekastandroiddongle.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.model.Client;
import com.wekast.wekastandroiddongle.model.ControllerAccessPoint;
import com.wekast.wekastandroiddongle.model.ControllerWifi;
import com.wekast.wekastandroiddongle.model.DongleService;

import java.util.List;

/**
 * Created by YEHUDA on 8/1/2016.
 */
//public class MainActivity extends AppCompatActivity implements ServiceCallbacks {
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "wekastdongle";
    EditText editTextAddress;
    EditText editTextPort;
    Button buttonConnect;
    Button buttonClear;
    public TextView response;


    public WifiManager wifiManager;

    Context context = this;
    DongleService dongleService;
    boolean isBound = false;

    ControllerWifi wifiController;
    ControllerAccessPoint accessPointController;

    boolean mBounded;

    ServiceConnection serviceConnection;
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
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiController = new ControllerWifi(wifiManager);
        accessPointController = new ControllerAccessPoint(wifiManager, wifiController);

        initViewElements();
        startAccessPoint();
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
//        if (isBound) {
//        myService.setCallbacks(null); // unregister
//            unbindService(serviceConnection);
//            isBound = false;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessPointController.setAccessPointEnabled(context, false);
        this.finish();
        Log.d(TAG, "MainActivity.onDestroy()");
    }

    private void connectToWifiHotspot() {

        wifiController.configureWifiConfig(getText(R.string.ssid).toString(), getText(R.string.pass).toString());
//        wifiController.connectToSelectedNetwork(getText(R.string.ssid).toString(), getText(R.string.pass).toString(),
//                wifiController.getWifiConfig());

        // Remove wifi configuration with default dongle access point SSID if exists
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID.equals(wifiController.wifiConfig.SSID)) {
                wifiManager.removeNetwork(i.networkId);
            }
        }

        // Connect to default dongle access point
        int netId = wifiController.addWifiConfiguration();
        if (netId != -1) {
            List<WifiConfiguration> list2 = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list2) {
                if (i.SSID != null && i.SSID.equals(wifiController.wifiConfig.SSID)) {
                    wifiController.disconnectFromWifi();
                    wifiController.enableDisableWifiNetwork(i.networkId, true);
                    wifiController.reconnectToWifi();
                    Log.d(TAG, "MainActivity.connectToWifiHotspot(): connected to "
                            + wifiController.wifiConfig.SSID + " with netId " + netId);
                    break;
                }
            }
        }

    }

    /**
     * Ititialization of view elements with listeners
     */
    private void initViewElements() {
        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        response = (TextView) findViewById(R.id.responseTextView);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Client myClient = new Client(editTextAddress.getText()
                        .toString(), Integer.parseInt(editTextPort
                        .getText().toString()), response);
                myClient.execute();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                response.setText("");
//                if (isBound) {
//                    dongleService.upload();
//                }

            }
        });
    }



//    private boolean isMyServiceRunning() {
////        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
////        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
////            if ("com.ehuda.dongle.model.DongleService".equals(service.service.getClassName())) {
////                return true;
////            }
////        }
////        return false;
//
//        String serviceName = "com.wekast.wekastandroiddongle.model.DongleService";
//        boolean serviceRunning = false;
//        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(Integer.MAX_VALUE);
//        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
//        while (i.hasNext()) {
//            ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i
//                    .next();
//            if(runningServiceInfo.service.getClassName().equals(serviceName)){
//                serviceRunning = true;
////                if(runningServiceInfo.service.foreground)
////                {
////                    //service run in foreground
////                }
//            }
//        }
//        return serviceRunning;
//    }

//    private void registerDongleBroadcastReceiver() {
//        dongleReceiver = new DongleBroadcastReceiver();
//        final IntentFilter filters = new IntentFilter();
//        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        filters.addAction("android.net.wifi.STATE_CHANGE");
//        super.registerReceiver(dongleReceiver, filters);
//        wifiManager.startScan();
//    }

    /**
     * Starts access point
      */
    private void startAccessPoint() {
        accessPointController.configureWifiConfig(getResources().getString(R.string.ssid),
                getResources().getString(R.string.pass));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                Log.d(TAG, "MainActivity.startAccessPoint() Settings.System.canWrite(context)? true");
            } else {
                Intent grantIntent = new   Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(grantIntent);
                Log.d(TAG, "MainActivity.startAccessPoint() Settings.System.canWrite(context)? false");
            }
        }

        // Turn off wifi before enabling Access Point
        if (wifiController.isWifiOn(context)) {
            wifiController.turnOnOffWifi(context, false);
        }

        // Turn on access point
        accessPointController.setAccessPointEnabled(context, true);

        // If not to wait application crashes
        accessPointController.waitAccessPoint();
    }

}
