package com.wekast.wekastandroiddongle.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.services.DongleService;
import com.wekast.wekastandroiddongle.services.IsWiFiConnectedService;

import static com.wekast.wekastandroiddongle.Utils.Utils.APP_PATH;

/**
 * Created by ELAD on 10/23/2016.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static Context context;
    private static Activity activity;
    private TextView textView;
    private LocalBroadcastManager mLocalBroadcastManager;

    private FrameLayout logoFrame;
    private ImageView slideImgView;
    private VideoView videoView;
    private TextView loggerView;
    private Bitmap bmp;

    public static Context getAppContext() {
        return FullscreenActivity.context;
    }

    public static Activity getMainActivity() {
        return FullscreenActivity.activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FULLSCREEN
//        getSupportActionBar().hide();       // hide action bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);


        FullscreenActivity.context = getApplicationContext();
        FullscreenActivity.activity = this;
        setContentView(R.layout.activity_fullscreen);

        if (requestSettingsPermissions()) {
            startService(new Intent(this, DongleService.class));
        }

        textView = (TextView) findViewById(R.id.logger);
        textView.setMovementMethod(new ScrollingMovementMethod());

//        final Button button = (Button) findViewById(R.id.testButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // TODO: add button on/off AP, and on/off WiFi for testing application
//            }
//        });

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        logoFrame = (FrameLayout) findViewById(R.id.logoFrame);
        slideImgView = (ImageView) findViewById(R.id.slideIMG);
        videoView = (VideoView) findViewById(R.id.videoView);
        loggerView = (TextView) findViewById(R.id.logger);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("INTENT_FILTER_NAME"));
        super.onResume();
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            final String curCommand = intent.getStringExtra("command");
            if (curCommand.equals("slide")) {
                final String curSlide = intent.getStringExtra("slide");
                final String curAnimation = intent.getStringExtra("animation");

                // if slide
//            restore background logoFrame
                logoFrame.setBackgroundColor(Color.rgb(0, 0, 0));
                loggerView.setVisibility(View.INVISIBLE);

                videoView.setVideoPath(APP_PATH + "cash/animations/slide" + curSlide + "_animation" + curAnimation + ".mp4");
                slideImgView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoView.start();

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        bmp = BitmapFactory.decodeFile(APP_PATH + "cash/animations/slide" + curSlide + "_animation" + curAnimation + ".jpg");
                        slideImgView.setImageBitmap(bmp);
                        slideImgView.setVisibility(View.VISIBLE);
                    }
                });
            }

            if (curCommand.equals("stop")) {
                videoView.setVisibility(View.INVISIBLE);
                slideImgView.setVisibility(View.INVISIBLE);
                FrameLayout logoFrame = (FrameLayout) findViewById(R.id.logoFrame);
                logoFrame.setBackgroundColor(Color.rgb(255, 255, 255));
                loggerView.setVisibility(View.VISIBLE);
            }

        }
    };

    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, DongleService.class));
        stopService(new Intent(this, IsWiFiConnectedService.class));
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

}
