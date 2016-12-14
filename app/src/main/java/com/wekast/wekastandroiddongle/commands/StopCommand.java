package com.wekast.wekastandroiddongle.commands;

import android.app.Activity;
import android.widget.TextView;

import com.wekast.wekastandroiddongle.R;
import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.activity.FullscreenActivity;
import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.wekast.wekastandroiddongle.Utils.Utils.APP_PATH;
import static com.wekast.wekastandroiddongle.Utils.Utils.CASH_DIRECTORY;

public class StopCommand implements ICommand {

    private CommandController controller;
    private Activity mainActivity = FullscreenActivity.getMainActivity();
    private TextView loggerView = (TextView) mainActivity.findViewById(R.id.logger);

    public StopCommand(CommandController controller) {
        this.controller = controller;
    }

    @Override
    public Answer execute() {
        controller.getService().stopPresentation();
        File file = new File(APP_PATH + "presentation.ezs");
        if(file.delete())
            printInfoMessage("DONGLE CONNECTED\n\nWAITING PRESENTATION\n\n");
//            logToTextView("Presentation", "removed");
        // TODO: clear cash directory
        Utils.clearWorkDirectory(APP_PATH + CASH_DIRECTORY);
        return new StopAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {
    }

    @Override
    public String getCommand() {
        return "stop";
    }

//    private void logToTextView(final String message, final String variable) {
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                loggerView.append(message + ": " + variable + "\n");
//            }
//        });
//    }

    private void printInfoMessage(final String message) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loggerView.setText(message);
            }
        });
    }

}
