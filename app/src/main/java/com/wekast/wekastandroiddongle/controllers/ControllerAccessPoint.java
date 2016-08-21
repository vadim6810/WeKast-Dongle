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
    private static Method setWifiApEnabled;
    private static Method isWifiApEnabled;

    public WifiManager wifiManager;
    public WifiConfiguration wifiConfig;

    static {
        // lookup methods and fields not defined publicly in the SDK.
        Class<?> cls = WifiManager.class;
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("setWifiApEnabled")) {
                setWifiApEnabled = method;
            }
            if (methodName.equals("isWifiApEnabled")) {
                isWifiApEnabled = method;
            }
        }
    }

    public ControllerAccessPoint(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

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

    public void waitAccessPointTurnOn() {
        while (!isAccessPointEnabled()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "ControllerAccessPoint.waitAccessPoint():  " + e);
            }
        }
        Log.d(TAG, "ControllerAccessPoint.waitAccessPointTurnOn() isAccessPointEnabled - true");
    }

//    public void waitAccessPointTurnOff() {
//        while (isAccessPointEnabled()) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.d(TAG, "ControllerAccessPoint.waitAccessPointTurnOn() isAccessPointEnabled - false");
//    }

    public boolean isAccessPointEnabled() {
        Boolean isAccessPointEnabled = false;
        try {
            isAccessPointEnabled = (Boolean) WifiManager.class.getDeclaredMethod("isWifiApEnabled").invoke(wifiManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return isAccessPointEnabled;
    }

}
