package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.Utils.Utils;
import com.wekast.wekastandroiddongle.controllers.CommandController;
import com.wekast.wekastandroiddongle.controllers.WifiController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/15/2016.
 */

public class Config implements ICommand {
    private CommandController controller;

    public Config(CommandController controller) {
        this.controller = controller;
    }

    private String ssid;
    private String password;
    @Override
    public String execute() {
        WifiController wifiController = controller.getService().getWifiController();
        wifiController.saveWifiConfig(ssid, password);
        // TODO switch state of wifi
        //wifiController.changeState();
        return null;
    }

    @Override
    public void parseArgs(JSONObject args) throws JSONException {
        ssid = args.getString("ssid");
        password = args.getString("password");
    }
}
