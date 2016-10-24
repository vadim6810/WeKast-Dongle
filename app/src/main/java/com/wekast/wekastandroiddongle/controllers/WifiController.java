package com.wekast.wekastandroiddongle.controllers;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ELAD on 10/14/2016.
 */

public class WifiController {

    private static final String AP_SSID_KEY = "ACCESS_POINT_SSID_ON_APP";
    private static final String AP_PASS_KEY = "ACCESS_POINT_PASS_ON_APP";
    private static final String TAG = "dongle.wekast";

    private static Method setWifiApEnabled;
    private static Method isWifiApEnabled;
    private static Method getWifiApConfiguration;
    private static Method setWifiApConfiguration;

    private WifiState curWifiState = WifiState.WIFI_STATE_OFF;

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

    private static boolean isWifiApEnabled(WifiManager wifiManager) {
        try {
            return (boolean) isWifiApEnabled.invoke(wifiManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static WifiConfiguration getWifiApConfiguration(WifiManager wifiManager) {
        try {
            return (WifiConfiguration) getWifiApConfiguration.invoke(wifiManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean setWifiApConfiguration(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        try {
            return (boolean) setWifiApConfiguration.invoke(wifiManager, wifiConfiguration);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

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
            if (methodName.equals("getWifiApConfiguration")) {
                getWifiApConfiguration = method;
                getWifiApConfiguration.setAccessible(true);
            }
            if (methodName.equals("setWifiApConfiguration")) {
                setWifiApConfiguration = method;
                setWifiApConfiguration.setAccessible(true);
            }
        }
    }

    private final boolean wifiEnabled;
    private final WifiManager wifiManager;
    private Context context;
    private WifiConfiguration oldConfig;
    Activity mainActivity = FullscreenActivity.getMainActivity();
    TextView textView = (TextView) mainActivity.findViewById(R.id.logger);

    public WifiController(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // Сохраняем старые настройки точки доступа
        oldConfig = getWifiApConfiguration(wifiManager);
        // Сохраняем состояние Wifi
        wifiEnabled = wifiManager.isWifiEnabled();
    }

    private WifiConfiguration configureWifi() {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "wekast";
        wifiConfig.preSharedKey = "12345678";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        return wifiConfig;
    }

    private WifiConfiguration configureWifi(String ssid, String pass) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + pass + "\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        return wifiConfig;
    }

    /**
     * Start Access Point on Dongle with default settings
     *
     * @return
     */
    private boolean startAP() {
        boolean result = isWifiApEnabled(wifiManager) || setWifiApEnabled(wifiManager, configureWifi(), true);
        logToTextView("Accesss Point started", String.valueOf(result));
        return result;
    }

    public boolean stopAP() {
        boolean result = setWifiApEnabled(wifiManager, oldConfig, false);
        logToTextView("Accesss Point stopped", String.valueOf(result));
        return result;
    }

    /**
     * Connect to Access Point on Client (Android or iOs)
     *
     * @return
     */
    public boolean startConnection() {
        // think where to put
//        wifiController.changeState(WifiController.WifiState.WIFI_STATE_CONNECT);

        stopAP();
        wifiManager.setWifiEnabled(true);
        // wait wifi module loading
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String curSsid = Utils.getFieldSP(context,AP_SSID_KEY);
        String curPass = Utils.getFieldSP(context,AP_PASS_KEY);
        WifiConfiguration wifiConfig = configureWifi(curSsid, curPass);

        int networkId = wifiManager.addNetwork(wifiConfig);
        if (networkId < 0) {
            throw new RuntimeException("coudn't add network " + curSsid);
        }
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();

        logToTextView("Connected to", curSsid);
        return true;
    }

    public WifiState getSavedWifiState() {
        return curWifiState;
    }

    public void saveWifiConfig(String ssid, String pass) {
        Utils.setFieldSP(context, AP_SSID_KEY, ssid);
        Utils.setFieldSP(context, AP_PASS_KEY, pass);
    }

    public void restore() {
        // TODO restore wifi settings back
        if (isWifiApEnabled(wifiManager)) {
            stopAP();
        }
        wifiManager.setWifiEnabled(wifiEnabled);
        setWifiApConfiguration(wifiManager, oldConfig);
    }

    public void changeState(WifiState wifiState) {
        //TODO: in progress
        if (wifiState == WifiState.WIFI_STATE_CONNECT) {
            // TODO: set curState WIFI_STARE_CONNECTING. check if connection is established set WIFI_STATE_CONNECTED
            curWifiState = WifiState.WIFI_STATE_CONNECT;
        } else if (wifiState == WifiState.WIFI_STATE_AP) {
            wifiManager.setWifiEnabled(false);
            startAP();
            curWifiState = WifiState.WIFI_STATE_AP;
        }
    }

    public enum WifiState {
        WIFI_STATE_OFF,
        WIFI_STATE_AP,
        WIFI_STATE_CONNECT
    }

    private void logToTextView(final String message, final String variable) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(message + ": " + variable + "\n");
            }
        });
    }

}
