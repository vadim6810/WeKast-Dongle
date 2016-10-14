package com.wekast.wekastandroiddongle.models;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.wekast.wekastandroiddongle.Utils.Loger;
import com.wekast.wekastandroiddongle.wifiControllers.ControllerAccessPoint;
import com.wekast.wekastandroiddongle.wifiControllers.ControllerWifi;
import com.wekast.wekastandroiddongle.Utils.Utils;

import java.util.List;

/**
 * Created by ELAD on 8/20/2016.
 */
public class DongleWifi {

    private static final String TAG = "wekastdongle";
    private Loger log = Loger.getInstance();
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
        this.accessPointController = new ControllerAccessPoint(wifiManager);
    }

    public boolean connectToAccessPoint(){
        // Disable access point
        accessPointController.setAccessPointEnabled(mainActivity, false);

        // wait more while access point on application is loading
//        accessPointController.waitAccessPointTurnOff();

        // Configure WifiConfiguration for default dongle access point
        String curSsid = Utils.getFieldSP(mainActivity, "accessPointSSID");
        String curPass = Utils.getFieldSP(mainActivity, "accessPointPASS");
        wifiController.configureWifiConfig(curSsid, curPass);

        // Switch on wifi
        wifiController.turnOnOffWifi(mainActivity, true);

        // Wait while wifi module is loading
//        wifiController.waitWhileWifiTurnOnOff();

        Log.i(TAG, "DongleWifi.connectToAccessPoint() isWifiOn: " + wifiController.isWifiOn(mainActivityContext));
        log.createLogger("DongleWifi.connectToAccessPoint() isWifiOn: " + wifiController.isWifiOn(mainActivityContext));

        // Remove wifi configuration with default dongle access point SSID if exists
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
                    log.createLogger("MainActivity.connectToWifiHotspot(): connected to "
                            + wifiController.wifiConfig.SSID + " with netId " + netId);
                    break;
                }
            }
        }

        // Wait while wifi establish connection
       // waitWifiConnect();
        showMessage("Connected to WiFi " + curSsid);
        Log.d(TAG, "AccessPointService.onHandleIntent(): end ");
        log.createLogger("AccessPointService.onHandleIntent(): end ");
        return true;
    }

    private void waitWifiConnect() {
        // Wait while wifi starting
        while (!wifiController.isWifiOn(mainActivityContext)) {
            wifiController.waitWhileWifiTurnOnOff(1000);
        }

        long timeStart = System.currentTimeMillis();
        long timeEnd = System.currentTimeMillis();
        double timeElapsed = 0;
        Log.i(TAG, "DongleWifi.connectToAccessPoint() isWifiOn: " + wifiController.isWifiOn(mainActivityContext));
        log.createLogger("DongleWifi.connectToAccessPoint() isWifiOn: " + wifiController.isWifiOn(mainActivityContext));

        // Wait while wifi connecting
        while (!wifiController.isWifiConnected(mainActivity)) {
            wifiController.waitWhileWifiTurnOnOff(1000);
            timeEnd = System.currentTimeMillis();
            timeElapsed = (timeEnd - timeStart)/ 1000.0;

            // TODO: set to 180 ( 3 min )
            // If Dongle can't connect to Access Point on client, start one more time Dongle Access Point
//            if (timeElapsed > 30.0) {
            if (timeElapsed > 60.0) {
                DongleAccessPoint dongleAccessPoint = new DongleAccessPoint(mainActivity);
                dongleAccessPoint.createAccessPoint();
                // java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
                // Utils.toastShowBottom(mainActivityContext, "Default Access Point started");
                Log.d(TAG, "DongleWifi.waitWifiConnect() ");
                log.createLogger("DongleWifi.waitWifiConnect() ");
            }
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
