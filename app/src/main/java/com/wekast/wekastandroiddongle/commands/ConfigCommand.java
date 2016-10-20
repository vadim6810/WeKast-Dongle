package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.controllers.CommandController;
import com.wekast.wekastandroiddongle.controllers.WifiController;

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
        WifiController wifiController = controller.getService().getWifiController();
        wifiController.saveWifiConfig(ssid, password);
        // TODO switch state of wifi
        wifiController.changeState(WifiController.WifiState.WIFI_STATE_CONNECT);
        return new ConfigAnswer();
    }

    @Override
    public void parseArgs(JSONObject args) throws JSONException {
        ssid = args.getString("ssid");
        password = args.getString("password");
    }
}
