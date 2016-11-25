package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

public class FileCommand implements ICommand {

    private CommandController controller;
    private String fileSize;

    public FileCommand(CommandController controller) {
        this.controller = controller;
    }

    @Override
    public Answer execute() {
        return new FileAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {
        fileSize = jsonObject.getString("fileSize");
    }

    @Override
    public String getCommand() {
        return "file";
    }

    public String getFileSize() {
        return fileSize;
    }

}
