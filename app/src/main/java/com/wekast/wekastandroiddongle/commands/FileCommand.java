package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/23/2016.
 */

public class FileCommand implements ICommand {

    private CommandController controller;

    public FileCommand(CommandController controller) {
        this.controller = controller;
    }

    @Override
    public Answer execute() {
        return new FileAnswer();
    }

    @Override
    public void parseArgs(JSONObject args) throws JSONException {

    }

    @Override
    public String getCommand() {
        return "file";
    }
}
