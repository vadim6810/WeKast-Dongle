package com.wekast.wekastandroiddongle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.services.DongleService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private boolean serviceStatus = false;
    private static Context context;
    private static Activity activity;
    private TextView textView;

    public static Context getAppContext() {
        return FullscreenActivity.context;
    }

    public static Activity getMainActivity() {
        return FullscreenActivity.activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullscreenActivity.context = getApplicationContext();
        FullscreenActivity.activity = this;
        setContentView(R.layout.activity_fullscreen);

//        Button button = (Button) findViewById(R.id.dummy_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (serviceStatus) {
//                    stopDongleService();
//                } else {
//                    startDongleService();
//                }
//            }
//        });

        if (requestSettingsPermissions()) {
            startDongleService();
        }

        // add scroll to logger textView
        textView = (TextView) findViewById(R.id.logger);
        textView.setMovementMethod(new ScrollingMovementMethod());

        // test textView with scroll
//        String s = "";
//        for (int i = 0; i < 100; i++) {
//            s+= "line " + i + "\n";
//        }
//        textView.append(s);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//        serviceStatus = true;
        startService(new Intent(this, DongleService.class));
    }

    private void stopDongleService() {
//        serviceStatus = false;
        stopService(new Intent(this, DongleService.class));
    }

}
