package com.wekast.wekastandroiddongle.model;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class AccessPointService extends IntentService {

    private static final String TAG = "wekastdongle";
    Context context = this;
    WifiManager wifiManager;
    ControllerWifi wifiController;
    ControllerAccessPoint accessPointController;

    public AccessPointService() {
        super("APService");
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AccessPointService.onCreate()");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AccessPointService.onDestroy()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        wifiManager = (WifiManager) getSystemService(context.WIFI_SERVICE);
        wifiController = new ControllerWifi(wifiManager);
        accessPointController = new ControllerAccessPoint(wifiManager, wifiController);
        // Disable access point
        accessPointController.setAccessPointEnabled(context, false);

        // Prepare configuration for wifi connection
        String curSsid = Utils.getFieldSP(context, "accessPointSSID");
        String curPass = Utils.getFieldSP(context, "accessPointPASS");
        wifiController.configureWifiConfig(curSsid, curPass);

        // Switch on wifi
        wifiController.turnOnOffWifi(context, true);

        // Wait while wifi module is loading
        wifiController.waitWhileWifiLoading();

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if(list != null) {
            for (WifiConfiguration i : list) {
                if (i.SSID.equals(wifiController.wifiConfig.SSID)) {
                    wifiManager.removeNetwork(i.networkId);
                }
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
                    showMessage(curSsid);
                    break;
                }
            }
        }
        Log.d(TAG, "AccessPointService.onHandleIntent(): end ");
    }

    private void showMessage(final String ssid) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Utils.toastShow(getApplicationContext(), "Trying to connect to " + ssid);
            }
        });
    }
}
