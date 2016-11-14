package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 11/14/2016.
 */

public class PingCommand implements ICommand {

    private CommandController controller;

    public PingCommand(CommandController controller) {
        this.controller = controller;
    }

    @Override
    public Answer execute() {
        return new PingAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public String getCommand() {
        return "ping";
    }
}
