package com.wekast.wekastandroiddongle.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.controllers.SocketController;
import com.wekast.wekastandroiddongle.controllers.WifiController;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {



    private WifiController wifiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

//        int canWriteSettings = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS)
//        if (canWriteSettings == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_SETTINGS},
//                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//        }

        wifiController = new WifiController(getApplicationContext());
        SocketController socketController = new SocketController();
        if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_NONE) {
            boolean result = wifiController.startAP();
            if (result) {
                socketController.waitForTask();
            }
        } else if (wifiController.getSavedWifiState() == WifiController.WifiState.WIFI_STATE_CONNECTED) {
            wifiController.startConnection();
        }

    }


}
