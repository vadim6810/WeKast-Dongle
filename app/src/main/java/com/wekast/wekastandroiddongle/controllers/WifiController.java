package com.wekast.wekastandroiddongle.controllers;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.wekast.wekastandroiddongle.Utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ELAD on 10/14/2016.
 */

public class WifiController {
    private static Method setWifiApEnabled;
    private static Method isWifiApEnabled;

    static {
        // lookup methods and fields not defined publicly in the SDK.
        Class<?> cls = WifiManager.class;
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("setWifiApEnabled")) {
                setWifiApEnabled = method;
                setWifiApEnabled.setAccessible(true);
            }
            if (methodName.equals("isWifiApEnabled")) {
                isWifiApEnabled = method;
                isWifiApEnabled.setAccessible(true);
            }
        }
    }

    private static boolean setWifiApEnabled(WifiManager wifiManager, WifiConfiguration wifiConfiguration, boolean enabled) {
        try {
            return (boolean) setWifiApEnabled.invoke(wifiManager, wifiConfiguration, enabled);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isWifiApEnabled(WifiManager wifiManager) {
        try {
            return (boolean) isWifiApEnabled.invoke(wifiManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Context context;

    public WifiController(Context context) {
        this.context = context;
    }

    private WifiConfiguration configureWifi() {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "wekast";
        wifiConfig.preSharedKey = "12345678";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        return wifiConfig;
    }

    /**
     * Start Access Point on Dongle with default settings
     *
     * @return
     */
    public boolean startAP() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!isWifiApEnabled(wifiManager)) {
            return setWifiApEnabled(wifiManager, configureWifi(), true);
        } else {
            // TODO check settings
            return true;
        }
    }

    public boolean stopAP() {
        return setWifiApEnabled((WifiManager) context.getSystemService(Context.WIFI_SERVICE), configureWifi(), false);
    }

    /**
     * Connect to Access Point on Client (Android or iOs)
     *
     * @return
     */
    public boolean startConnection() {
        return false;
    }

    public WifiState getSavedWifiState() {
        return WifiState.WIFI_STATE_NONE;
    }

    public void saveWifiConfig(String ssid, String pass) {
        Utils.setFieldSP(context, "ACCESS_POINT_SSID_ON_APP", ssid);
        Utils.setFieldSP(context, "ACCESS_POINT_PASS_ON_APP", pass);
    }

    public static enum WifiState {
        WIFI_STATE_NONE,
        WIFI_STATE_CONNECTED
    }
}
