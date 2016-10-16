package com.wekast.wekastandroiddongle.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.services.DongleService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private boolean serviceStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Button button = (Button) findViewById(R.id.dummy_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceStatus) {
                    stopDongleService();
                } else {
                    startDongleService();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestSettingsPermissions()) {
            startDongleService();
        }
    }

    private boolean requestSettingsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(intent);
                return false;
            }
        }
        return true;
    }

    private void startDongleService() {
        serviceStatus = true;
        getApplicationContext().startService(new Intent(getApplicationContext(), DongleService.class));
    }

    private void stopDongleService() {
        serviceStatus = false;
        stopService(new Intent(getApplicationContext(), DongleService.class));
    }
}
