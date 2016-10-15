package com.wekast.wekastandroiddongle.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/15/2016.
 */

public class Config implements ICommand {
    private String ssid;
    private String password;
    @Override
    public String execute() {

        return null;
    }

    @Override
    public void parseArgs(JSONObject args) throws JSONException {
        ssid = args.getString("ssid");
        password = args.getString("password");
    }
}
