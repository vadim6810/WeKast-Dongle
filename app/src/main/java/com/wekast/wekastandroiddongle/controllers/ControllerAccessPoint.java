package com.wekast.wekastandroiddongle.controllers;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.wekast.wekastandroiddongle.Utils.Loger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is use to handle all Hotspot related information.
 */
public class ControllerAccessPoint {
    private static final String TAG = "wekastdongle";
    private Loger log = Loger.getInstance();
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
            // try and remove if not needed. On dongle InvocationTargetException
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            boolean curStatus = (Boolean) setWifiApEnabled.invoke(wifiManager, wifiConfig, enabled);
            Log.d(TAG, "ControllerAccessPoint.setWifiApEnabled(): " + curStatus);
            log.createLogger("ControllerAccessPoint.setWifiApEnabled(): " + curStatus);
            return curStatus;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "ControllerAccessPoint.setWifiApEnabled(): IllegalAccessException " + e.getMessage());
            log.createLogger("ControllerAccessPoint.setWifiApEnabled(): IllegalAccessException: " + e.getMessage());
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, "ControllerAccessPoint.setWifiApEnabled(): InvocationTargetException " + e.getMessage());
//            log.createLogger("ControllerAccessPoint.setWifiApEnabled(): InvocationTargetException: " + e.getMessage());

            // test
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.createLogger("ControllerAccessPoint.setWifiApEnabled(): InvocationTargetException: " + errors);
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
                Log.d(TAG, "ControllerAccessPoint.waitAccessPoint():  " + e.getMessage());
                log.createLogger("ControllerAccessPoint.waitAccessPoint() InterruptedException: " + e.getMessage());
            }
        }
//        Log.d(TAG, "ControllerAccessPoint.waitAccessPointTurnOn() isAccessPointEnabled - true");
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
            Log.d(TAG, "ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + e.getMessage());
            log.createLogger("ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, "ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + e.getMessage());
//            log.createLogger("ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + e.getMessage());

            // test
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.createLogger("ControllerAccessPoint.setWifiApEnabled(): InvocationTargetException: " + errors);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.d(TAG, "ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + e.getMessage());
            log.createLogger("ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + e.getMessage());
        }
//        Log.d(TAG, "ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + isAccessPointEnabled);
//        Loger.getInstance().createLogger("ControllerAccessPoint.isAccessPointEnabled() isAccessPointEnabled: " + isAccessPointEnabled);
        return isAccessPointEnabled;
    }

}
