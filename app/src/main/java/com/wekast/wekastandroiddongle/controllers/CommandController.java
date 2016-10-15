package com.wekast.wekastandroiddongle.controllers;

import com.wekast.wekastandroiddongle.commands.Config;
import com.wekast.wekastandroiddongle.commands.ICommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/15/2016.
 */

public class CommandController {


    public ICommand parseCommand(String commandStr) throws Exception {
        ICommand command;
        try {
            JSONObject jsonRootObject = new JSONObject(commandStr);
            String commandName = jsonRootObject.getString("command");
            switch (commandName) {
                case "config":
                    command = new Config();
                    break;
                default:
                    // TODO: make self class exception
                    throw new Exception("Unknown command");
            }
            command.parseArgs(jsonRootObject.getJSONObject("args"));

        } catch (JSONException e) {
            // TODO throw exception
            e.printStackTrace();
        }
        return null;
    }
}
