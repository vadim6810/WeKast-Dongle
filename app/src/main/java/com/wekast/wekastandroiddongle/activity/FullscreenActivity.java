package com.wekast.wekastandroiddongle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.controllers.SocketController;
import com.wekast.wekastandroiddongle.controllers.WifiController;
import com.wekast.wekastandroiddongle.services.DongleService;

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

        getApplicationContext().startService(new Intent(getApplicationContext(), DongleService.class));

    }


}
