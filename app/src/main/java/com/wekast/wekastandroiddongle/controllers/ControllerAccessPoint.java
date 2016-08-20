package com.wekast.wekastandroiddongle.controllers;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is use to handle all Hotspot related information.
 */
public class ControllerAccessPoint {
    private static final String TAG = "wekastClient";
    private static Method getWifiApState;
    private static Method isWifiApEnabled;
    private static Method setWifiApEnabled;
    private static Method getWifiApConfiguration;

    public WifiManager wifiManager;
    public WifiConfiguration wifiConfig;
//    public ControllerWifi wifiController;

    static {
        // lookup methods and fields not defined publicly in the SDK.
        Class<?> cls = WifiManager.class;
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getAccessPointState")) {
                getWifiApState = method;
            } else if (methodName.equals("isAccessPointEnabled")) {
                isWifiApEnabled = method;
            } else if (methodName.equals("setWifiApEnabled")) {
                setWifiApEnabled = method;
            } else if (methodName.equals("getAccessPointConfiguration")) {
                getWifiApConfiguration = method;
            }
        }
    }

    public static boolean isApSupported() {
        return (getWifiApState != null && isWifiApEnabled != null
                && setWifiApEnabled != null && getWifiApConfiguration != null);
    }

    public ControllerAccessPoint(WifiManager wifiManager, ControllerWifi wifiController) {
        this.wifiManager = wifiManager;
//        this.wifiController = wifiController;
    }

    // ERROR not working
//    public boolean isAccessPointEnabled() {
//        try {
//            return (Boolean) isWifiApEnabled.invoke(wifiManager);
//        } catch (Exception e) {
//            Log.d(TAG, "ControllerAccessPoint.isAccessPointEnabled(): " + e.toString(), e); // shouldn't happen
//            return false;
//        }
//    }

//    public int getAccessPointState() {
//        try {
//            return (Integer) getWifiApState.invoke(wifiManager);
//        } catch (Exception e) {
//            Log.d(TAG, "ControllerAccessPoint.getAccessPointState(): " + e.toString(), e); // shouldn't happen
//            return -1;
//        }
//    }

//    public WifiConfiguration getAccessPointConfiguration() {
//        try {
//            return (WifiConfiguration) getWifiApConfiguration.invoke(wifiManager);
//        } catch (Exception e) {
//            Log.d(TAG, "ControllerAccessPoint.getAccessPointConfiguration(): " + e.toString(), e); // shouldn't happen
//            return null;
//        }
//    }

    public boolean setAccessPointEnabled(Context context, boolean enabled)  {
        try {
            // TODO: when disable - setWifiApEnabled.invoke(wifiManager, null, enabled)
            boolean curStatus = (Boolean) setWifiApEnabled.invoke(wifiManager, wifiConfig, enabled);
            Log.d(TAG, "ControllerAccessPoint.setWifiApEnabled(): " + curStatus);
            return curStatus;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "ControllerAccessPoint.setWifiApEnabled(): IllegalAccessException " + e);
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, "ControllerAccessPoint.setWifiApEnabled(): InvocationTargetException " + e);
            return false;
        }
    }

    /**
     * Function that configuretes WifiConfiguration for connecting to hotspot
     *
     * @return configured WifiConfiguration
     */
    public void configureWifiConfig (String ssid, String pass) {
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = ssid;
        wifiConfig.preSharedKey = pass;
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        // Log.d(TAG, "ControllerAccessPoint.configureWifiConfig():\n" + wifiConfig);
    }

    public void waitAccessPoint() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "ControllerAccessPoint.waitAccessPoint():  " + e);
        }
    }

}
