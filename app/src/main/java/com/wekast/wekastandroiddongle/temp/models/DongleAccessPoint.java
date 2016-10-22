package com.wekast.wekastandroiddongle.temp.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.Utils.Loger;
import com.wekast.wekastandroiddongle.temp.wifiControllers.ControllerAccessPoint;
import com.wekast.wekastandroiddongle.temp.wifiControllers.ControllerWifi;

/**
 * Created by ELAD on 8/20/2016.
 */
public class DongleAccessPoint {

    private static final String TAG = "wekastdongle";
    private Loger log = Loger.getInstance();
    private Context mainActivityContext = null;
    private Activity mainActivity = null;
    private WifiManager wifiManager = null;
    private ControllerWifi wifiController = null;
    private ControllerAccessPoint accessPointController = null;

    public DongleAccessPoint(Activity activity){
        this.mainActivity = activity;
        this.mainActivityContext = mainActivity.getApplicationContext();
        this.wifiManager = (WifiManager) mainActivityContext.getSystemService(mainActivityContext.WIFI_SERVICE);
        this.wifiController = new ControllerWifi(wifiManager);
        this.accessPointController = new ControllerAccessPoint(wifiManager);
    }

    /**
     * Function that creates and turn on AccessPoint with default ssid and pass
     *
     * @return true at the end if access point is started
     */
    public boolean createAccessPoint(){
        // Configure access point
        accessPointController.configureWifiConfig(mainActivity.getResources().getString(R.string.ssid),
                mainActivity.getResources().getString(R.string.pass));

        // TODO: check how it works on android 5, 6 first load. Do user have to grant rights to application?
        // Compatibility with android 5, android 6
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(mainActivity)) {
                Log.d(TAG, "MainActivity.startAccessPoint() Settings.System.canWrite(context)? true");
                log.createLogger("MainActivity.startAccessPoint() Settings.System.canWrite(context)? true");
            } else {
                Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                mainActivity.startActivity(grantIntent);
                Log.d(TAG, "MainActivity.startAccessPoint() Settings.System.canWrite(context)? false");
                log.createLogger("MainActivity.startAccessPoint() Settings.System.canWrite(context)? false");
            }
        }

        // Maybe not needed becouse on dongle on start wifi adapter is disabled
        // Turn off wifi before enabling Access Point
//        wifiController.turnOnOffWifi(mainActivity, false);
//        while (wifiController.isWifiOn(mainActivity)) {
//            wifiController.waitWhileWifiTurnOnOff(1000);
//        }

        // Turn on access point
        accessPointController.setAccessPointEnabled(mainActivity, true);

        // Wait while Access Point loading
        accessPointController.waitAccessPointTurnOn();
        return true;
    }

    public boolean destroyAccessPoint()
    {
        accessPointController.setAccessPointEnabled(mainActivity, false);
        // TODO: restore wifi module settings before applications was start
        // this can be that wifi was enebled or disabled, or Access point was enebled
        return true;
    }
}
