package com.wekast.wekastandroiddongle.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;

public class WifiConnected extends BroadcastReceiver {

    private static final String TAG = "WifiConnected";
    private static final Handler handler = null;

    public WifiConnected() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // TODO: need check null because in airplane mode it will be null
        boolean isConnected = (netInfo != null && netInfo.isConnected());

        if (!Utils.getContainsSP(context, "WIFI_IS_CONNECTED_LAST_STATE")) {
            Utils.setFieldSP(context, "WIFI_IS_CONNECTED_LAST_STATE", "" + isConnected);
        }

        Boolean lastStateIsConnected = Boolean.valueOf(Utils.getFieldSP(context, "WIFI_IS_CONNECTED_LAST_STATE"));
        Log.e(TAG, "isConnected: " + isConnected + " lastStateIsConnected: " + lastStateIsConnected);
        if (lastStateIsConnected != isConnected) {
            Activity activity = FullscreenActivity.getMainActivity();
            TextView textV1 = (TextView) activity.findViewById(R.id.logger);
            Utils.setFieldSP(context, "WIFI_IS_CONNECTED_LAST_STATE", "" + isConnected);
            if (isConnected) {
                // Toast.makeText(context, "CONNECTED", Toast.LENGTH_SHORT).show();
                textV1.setText("CONNECTED\n\nWAITING PRESENTATION\n\n");
                Log.e(TAG, "CONNECTED WAITING");
            } else {
                // Toast.makeText(context, "DISCONNECTED", Toast.LENGTH_SHORT).show();
                textV1.setText("DISCONNECTED\n\nWAITING\n\n");
                Log.e(TAG, "DISCONNECTED WAITING CONNECTION");
                // TODO: close presentation if opened . Test maybe some other cases not needed to stop presentation
                stopPresentation();
            }
        }
    }

    private void stopPresentation() {
        Activity activity = FullscreenActivity.getMainActivity();
        FrameLayout logoFrame = (FrameLayout) activity.findViewById(R.id.logoFrame);
        ImageView icLogo = (ImageView) activity.findViewById(R.id.ic_logo);
        ImageView slideImgView = (ImageView) activity.findViewById(R.id.slideIMG);
        VideoView videoView = (VideoView) activity.findViewById(R.id.videoView);
        TextView loggerView = (TextView) activity.findViewById(R.id.logger);

        videoView.setVisibility(View.INVISIBLE);
        slideImgView.setVisibility(View.INVISIBLE);
        logoFrame.setBackgroundColor(Color.rgb(255, 255, 255));
        icLogo.setVisibility(View.VISIBLE);
        loggerView.setVisibility(View.VISIBLE);
    }

}
