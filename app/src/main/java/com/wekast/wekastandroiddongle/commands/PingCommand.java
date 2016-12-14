package com.wekast.wekastandroiddongle.commands;

import android.app.Activity;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

public class PingCommand implements ICommand {

    private CommandController controller;
    private Activity mainActivity = FullscreenActivity.getMainActivity();
    private TextView loggerView = (TextView) mainActivity.findViewById(R.id.logger);

    public PingCommand(CommandController controller) {
        this.controller = controller;
    }

    @Override
    public Answer execute() {
//        printInfoMessage("DONGLE CONNECTED");
        return new PingAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public String getCommand() {
        return "ping";
    }

//    private void printInfoMessage(final String message) {
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                loggerView.setText(message);
//            }
//        });
//    }

}
