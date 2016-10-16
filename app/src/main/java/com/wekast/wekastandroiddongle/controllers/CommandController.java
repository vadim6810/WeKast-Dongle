package com.wekast.wekastandroiddongle.controllers;

import com.wekast.wekastandroiddongle.commands.Config;
import com.wekast.wekastandroiddongle.commands.ICommand;
import com.wekast.wekastandroiddongle.services.DongleService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/15/2016.
 */

public class CommandController {

    private DongleService service;

    public DongleService getService() {
        return service;
    }

    public CommandController(DongleService service) {
        this.service = service;
    }

    private ICommand parseCommand(String commandStr) throws Exception {
        ICommand command;
        try {
            JSONObject jsonRootObject = new JSONObject(commandStr);
            String commandName = jsonRootObject.getString("command");
            switch (commandName) {
                case "config":
                    command = new Config(this);
                    break;
                default:
                    // TODO: make self class exception
                    throw new Exception("Unknown command");
            }
            command.parseArgs(jsonRootObject.getJSONObject("args"));
            return command;
        } catch (JSONException e) {
            // TODO throw exception
            e.printStackTrace();
            throw e;
        }
    }

    public String processTask(String task) {
//        try {
//            ICommand command = parseCommand(task);
//            return command.execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//            // TODO: Good JSON Answer
//            return "bad command";
//        }
        return "Result";
    }
}
