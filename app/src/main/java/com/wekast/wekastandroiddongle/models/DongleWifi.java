package com.wekast.wekastandroiddongle.models;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.wekast.wekastandroiddongle.controllers.ControllerAccessPoint;
import com.wekast.wekastandroiddongle.controllers.ControllerWifi;
import com.wekast.wekastandroiddongle.Utils.Utils;

import java.util.List;

/**
 * Created by ELAD on 8/20/2016.
 */
public class DongleWifi {

    private static final String TAG = "wekastdongle";
    private Context mainActivityContext = null;
    private Activity mainActivity = null;
    private WifiManager wifiManager = null;
    private ControllerWifi wifiController = null;
    private ControllerAccessPoint accessPointController = null;

    public DongleWifi(Activity activity) {
        this.mainActivity = activity;
        this.mainActivityContext = mainActivity.getApplicationContext();
        this.wifiManager = (WifiManager) mainActivityContext.getSystemService(mainActivityContext.WIFI_SERVICE);
        this.wifiController = new ControllerWifi(wifiManager);
        this.accessPointController = new ControllerAccessPoint(wifiManager, wifiController);
    }

    public boolean connectToAccessPoint(){
        // Disable access point
        accessPointController.setAccessPointEnabled(mainActivity, false);

        // Prepare configuration for wifi connection
        String curSsid = Utils.getFieldSP(mainActivity, "accessPointSSID");
        String curPass = Utils.getFieldSP(mainActivity, "accessPointPASS");
        wifiController.configureWifiConfig(curSsid, curPass);

        // Switch on wifi
        wifiController.turnOnOffWifi(mainActivity, true);

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

        // Connect to access point of application
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

        isWifiLoaded();
        showMessage("Connected to WiFi " + curSsid);
        Log.d(TAG, "AccessPointService.onHandleIntent(): end ");
        return true;
    }

    private void isWifiLoaded() {
        if (!wifiController.isWifiConnected(mainActivity)) {
            wifiController.waitWhileWifiLoading(1000);
            isWifiLoaded();
        }

    }

    private void showMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Utils.toastShowBottom(mainActivityContext, message);
            }
        });
    }
}
