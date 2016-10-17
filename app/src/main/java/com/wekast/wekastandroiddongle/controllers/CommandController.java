package com.wekast.wekastandroiddongle.controllers;

import android.util.Log;

import com.wekast.wekastandroiddongle.commands.Answer;
import com.wekast.wekastandroiddongle.commands.ConfigCommand;
import com.wekast.wekastandroiddongle.commands.ErrorAnswer;
import com.wekast.wekastandroiddongle.commands.ICommand;
import com.wekast.wekastandroiddongle.services.DongleService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/15/2016.
 */

public class CommandController {

    public static final String TAG = "Dongle";
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
                    command = new ConfigCommand(this);
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

    Answer processTask(String task) {
        try {
            ICommand command = parseCommand(task);
            return command.execute();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return new ErrorAnswer(e);
        }
    }
}
