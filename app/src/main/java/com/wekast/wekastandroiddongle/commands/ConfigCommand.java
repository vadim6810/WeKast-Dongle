package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/15/2016.
 */
public class ConfigCommand implements ICommand {

    private CommandController controller;

    public ConfigCommand(CommandController controller) {
        this.controller = controller;
    }

    private String ssid;
    private String password;

    @Override
    public Answer execute() {
        return new ConfigAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {
        ssid = jsonObject.getString("ssid");
        password = jsonObject.getString("password");
    }

    @Override
    public String getCommand() {
        return "config";
    }

    public String getSsid() {
        return ssid;
    }

    public String getPassword() {
        return password;
    }
}
