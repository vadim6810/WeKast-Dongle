package com.wekast.wekastandroiddongle.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.entities.Slide;
import com.wekast.wekastandroiddongle.services.DongleService;
import com.wekast.wekastandroiddongle.services.IsWiFiConnectedService;

import java.util.ArrayList;
import java.util.Map;

import static com.wekast.wekastandroiddongle.Utils.Utils.APP_PATH;

public class FullscreenActivity extends AppCompatActivity {

    private static Context context;
    private static Activity activity;
//    private TextView textView;
    private LocalBroadcastManager mLocalBroadcastManager;

    private FrameLayout logoFrame;
    private ImageView icLogo;
    private ImageView slideImgView;
    private VideoView videoView;
    private TextView loggerView;
    private ProgressDialog progressDialog;
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
        getSupportActionBar().hide();       // hide action bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        FullscreenActivity.context = getApplicationContext();
        FullscreenActivity.activity = this;
        setContentView(R.layout.activity_fullscreen);

        if (requestSettingsPermissions()) {
            startService(new Intent(this, DongleService.class));
        }

        loggerView = (TextView) findViewById(R.id.logger);
        loggerView.setMovementMethod(new ScrollingMovementMethod());

//        final Button button = (Button) findViewById(R.id.testButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // TODO: add button on/off AP, and on/off WiFi for testing application
//            }
//        });

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        logoFrame = (FrameLayout) findViewById(R.id.logoFrame);
        icLogo = (ImageView) findViewById(R.id.ic_logo);
        slideImgView = (ImageView) findViewById(R.id.slideIMG);
        videoView = (VideoView) findViewById(R.id.videoView);
//        loggerView = (TextView) findViewById(R.id.logger);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                mMessageReceiver, new IntentFilter("MAIN_WINDOW"));
        super.onResume();
    }

    // Handler for received Intents. This will be called whenever an Intent
    // is broadcasted
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            final String curCommand = intent.getStringExtra("command");
            if (curCommand.equals("slide")) {
                if (intent.getStringExtra("animation").equals(""))
                    showSlide(intent.getStringExtra("slide"));
                else
                    showSlide(intent.getStringExtra("slide"), intent.getStringExtra("animation"));
            }
            if (curCommand.equals("stop"))
                stopPresentation();
            if (curCommand.equals("show_progress_bar"))
                showProgressDialog(intent.getStringExtra("message"));
            if (curCommand.equals("hide_progress_bar"))
                hideProgressDialog();
        }
    };

    private void showSlide(final String curSlide) {
        logoFrame.setBackgroundColor(Color.rgb(0, 0, 0));
//        loggerView.setVisibility(View.INVISIBLE);
        icLogo.setVisibility(View.INVISIBLE);

        bmp = BitmapFactory.decodeFile(APP_PATH + "cash/slides/" + curSlide + ".jpg");
        slideImgView.setImageBitmap(bmp);
        slideImgView.setVisibility(View.VISIBLE);
    }

    private void showSlide(final String curSlide, final String curMedia) {
        logoFrame.setBackgroundColor(Color.rgb(0, 0, 0));
//        loggerView.setVisibility(View.INVISIBLE);
        icLogo.setVisibility(View.INVISIBLE);

        ArrayList<Slide> slidesList = Utils.slidesList;
        Slide slide = slidesList.get(Integer.valueOf(curSlide));
        Map<Integer, String> mediaTypes = slide.getMediaType();
        String curType = mediaTypes.get(Integer.valueOf(curMedia));

        if (curType.equals("animation")) {
            videoView.setVideoPath(APP_PATH + "cash/animations/slide" + curSlide + "_animation" + curMedia + ".mp4");
            slideImgView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    bmp = BitmapFactory.decodeFile(APP_PATH + "cash/animations/slide" + curSlide + "_animation" + curMedia + ".jpg");
                    slideImgView.setImageBitmap(bmp);
                    slideImgView.setVisibility(View.VISIBLE);
                }
            });
        }
        if (curType.equals("video")) {
            videoView.setVideoPath(APP_PATH + "cash/video/v" + curSlide + ".avi");
            slideImgView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    bmp = BitmapFactory.decodeFile(APP_PATH + "cash/slides/" + curSlide + ".jpg");
                    slideImgView.setImageBitmap(bmp);
                    slideImgView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void stopPresentation() {
        videoView.setVisibility(View.INVISIBLE);
        slideImgView.setVisibility(View.INVISIBLE);
        FrameLayout logoFrame = (FrameLayout) findViewById(R.id.logoFrame);
        logoFrame.setBackgroundColor(Color.rgb(255, 255, 255));
        icLogo.setVisibility(View.VISIBLE);
//        loggerView.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog.show(this, "Please wait", message, true);
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

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
