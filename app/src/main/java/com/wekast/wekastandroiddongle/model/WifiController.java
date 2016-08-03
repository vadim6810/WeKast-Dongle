package com.wekast.wekastandroiddongle.model;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by YEHUDA on 8/1/2016.
 */
public class WifiController {
    private static final String TAG = "wekastdongle";
    public WifiManager wifiManager;
    public WifiConfiguration wifiConfig;


    public WifiController(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    /**
     * Function check whether wifi is enabled
     *
     * @param context
     * @return
     */
    public boolean isWifiOn(Context context) {
        boolean isWifiOn = wifiManager.isWifiEnabled();
        Log.d(TAG, "MainActivity.isWifiOn(): " + isWifiOn);
        return isWifiOn;
    }

    /**
     * Function turns on or turns off wifi
     *
     * @param context
     * @param b
     */
    public void turnOnOffWifi(Context context, boolean b) {
        wifiManager.setWifiEnabled(b);
        Log.d(TAG, "MainActivity.turnOnOffWifi(): " + b);
    }

    /**
     * Function that configuretes WifiConfiguration for connecting to hotspot
     *
     * @return configured WifiConfiguration
     */
    public void configureWifiConfig(String ssid, String pass) {
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"".concat(ssid).concat("\"");
        wifiConfig.preSharedKey = "\"".concat(pass).concat("\"");
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        Log.d(TAG, "MainActivity.configureWifiConfig():\n" + wifiConfig);
    }

    public WifiConfiguration getWifiConfig() {
        return wifiConfig;
    }


    /* Function to disconnect from the currently connected WiFi AP.
    * @return true  if disconnection succeeded
    * 				 false if disconnection failed
    */
    public void disconnectFromWifi() {
        if (!wifiManager.disconnect()) {
            Log.d("TAG", "Failed to disconnect from network!");
        }
    }

    /**
     * Add WiFi configuration to list of recognizable networks
     *
     * @return networkId
     */
    public int addWifiConfiguration() {
        int networkId = wifiManager.addNetwork(wifiConfig);
        if (networkId == -1) {
            Log.d("TAG", "Failed to add network configuration!");
            return -1;
        }
        return networkId;
    }

    /**
     * Enable network to be connected
     */
    public void enableDisableWifiNetwork(int networkId, boolean b) {
        if (!wifiManager.enableNetwork(networkId, b)) {
            Log.d("TAG", "Failed to enable network!");
        }
    }

    /**
     * Connect to network
     */
    public void reconnectToWifi() {
        if (!wifiManager.reconnect()) {
            Log.d("TAG", "Failed to connect!");
        }
    }
}