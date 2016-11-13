package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 11/12/2016.
 */

public class StopCommand implements ICommand {

    private CommandController controller;

    public StopCommand(CommandController controller) {
        this.controller = controller;
    }

    @Override
    public Answer execute() {
        controller.getService().stopPresentation();
        return new StopAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {
    }

    @Override
    public String getCommand() {
        return "stop";
    }
}
