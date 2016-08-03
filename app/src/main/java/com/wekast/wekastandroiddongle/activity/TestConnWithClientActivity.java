package com.wekast.wekastandroiddongle.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.model.Client;
import com.wekast.wekastandroiddongle.model.DongleBroadcastReceiver;
import com.wekast.wekastandroiddongle.model.DongleService;
import com.wekast.wekastandroiddongle.model.WifiController;

import java.util.List;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class TestConnWithClientActivity extends AppCompatActivity {
    private static final String TAG = "wekastdongle";
    EditText editTextAddress;
    EditText editTextPort;
    Button buttonConnect;
    Button buttonClear;
    TextView response;

    WifiController wifiController;
    public WifiManager wifiManager;
    WifiConfiguration wifiConfig;

    Context context = this;
    ServiceConnection serviceConnection;
    DongleService testService;
    boolean isBound = false;

    DongleBroadcastReceiver dongleReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        registerDongleBroadcastReceiver();

        wifiController = new WifiController(wifiManager);
        connectToWifiHotspot();
        initViewElements();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "TestConnWithClientActivity.onStart()");
        startDongleService();
        Log.d(TAG, "TestConnWithClientActivity.onStart():  isMyServiceRunning():" + isMyServiceRunning());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "TestConnWithClientActivity.onPause()");
//        unregisterReceiver(dongleReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "TestConnWithClientActivity.onResume()");

//        registerReceiver(dongleReceiver, new IntentFilter(String.valueOf(WifiManager.WIFI_STATE_ENABLED)));
//        registerReceiver(dongleReceiver, new IntentFilter(String.valueOf(WifiManager.WIFI_STATE_DISABLED)));
//        registerReceiver(dongleReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "TestConnWithClientActivity.onStop()");
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        // TODO: check if service is stoped
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TestConnWithClientActivity.onDestroy()");
        this.finish();
        // TODO: save wifi status on startup. If wifi active -> don't switch off wifi
        // turnOnOffWifi(context, false);
        // TODO: destroy dongleBroadcastReceiver
    }

    private void startDongleService() {
        Intent intent = new Intent(this, DongleService.class);
        ComponentName name = startService(intent);
        Log.d(TAG, "TestConnWithClientActivity.startDongleService() ComponentName: " + name);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void connectToWifiHotspot() {

        wifiController.configureWifiConfig(getText(R.string.ssid).toString(), getText(R.string.pass).toString());
//        wifiController.connectToSelectedNetwork(getText(R.string.ssid).toString(), getText(R.string.pass).toString(),
//                wifiController.getWifiConfig());



        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            wifiManager.removeNetwork(i.networkId);
        }

        int netId = wifiController.addWifiConfiguration();
        if (netId != -1) {
            List<WifiConfiguration> list2 = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list2) {
                if (i.SSID != null && i.SSID.equals(wifiController.wifiConfig.SSID)) {
                    wifiController.disconnectFromWifi();
                    wifiController.enableDisableWifiNetwork(i.networkId, true);
                    wifiController.reconnectToWifi();
                    Log.d(TAG, "TestConnWithClientActivity.connectToWifiHotspot(): connected to "
                            + wifiController.wifiConfig.SSID + " with netId " + netId);
                    break;
                }
            }
        }
    }

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
                response.setText("");
                if (isBound) {
                    testService.upload();
                }
            }
        });

        bindDongleService();
    }

    private void bindDongleService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DongleService.MyBinder binder = (DongleService.MyBinder) service;
                testService = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                testService = null;
                isBound = false;
            }
        };
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.ehuda.dongle.model.DongleService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void registerDongleBroadcastReceiver() {
        dongleReceiver = new DongleBroadcastReceiver();
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        super.registerReceiver(dongleReceiver, filters);
        wifiManager.startScan();
    }
}
