package com.wekast.wekastandroiddongle.controllers;

import android.util.Log;

import com.wekast.wekastandroiddongle.commands.Answer;
import com.wekast.wekastandroiddongle.commands.ConfigCommand;
import com.wekast.wekastandroiddongle.commands.ErrorAnswer;
import com.wekast.wekastandroiddongle.commands.FileCommand;
import com.wekast.wekastandroiddongle.commands.ICommand;
import com.wekast.wekastandroiddongle.commands.PingCommand;
import com.wekast.wekastandroiddongle.commands.SlideCommand;
import com.wekast.wekastandroiddongle.commands.StopCommand;
import com.wekast.wekastandroiddongle.services.DongleService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON commands:
 * Request from client to dongle with new ssid and pass
 * {"command":"config","args":{"ssid":"wekast","password":"87654321"}}
 * Request from client to dongle that want to send file
 * {"command":"file","args":{"filesize":"650000"}}
 * Response from dongle on command "file" with socket port for transfer file
 * {"port":"9999","message":"ok","type":"file","device":"dongle"}
 * Request from client to dongle to view slide on dongle
 * {"command":"slide","args":{"slide":"1","animation":"1","video":"","audio":""}}
 * Response from dongle on command "slide"
 * {"message":"ok","type":"slide","device":"dongle"}
 * Request from client to dongle to stop showing presentation
 * {"command":"stop"}
 * Request from client to dongle check connection
 * {"command":"ping"}
 * Response from dongle on command "ping"
 * {"message":"ok","type":"ping","device":"dongle"}
 *
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

    public ICommand parseCommand(String commandStr) throws Exception {
        ICommand command;
        try {
            JSONObject jsonRootObject = new JSONObject(commandStr);
            String commandName = jsonRootObject.getString("command");
            switch (commandName) {
                case "config":
                    command = new ConfigCommand(this);
                    command.parseArgs(jsonRootObject.getJSONObject("args"));
                    break;
                case "file":
                    command = new FileCommand(this);
                    command.parseArgs(jsonRootObject.getJSONObject("args"));
                    break;
                case "slide":
                    command = new SlideCommand(this);
                    command.parseArgs(jsonRootObject.getJSONObject("args"));
                    break;
                case "stop":
                    command = new StopCommand(this);
                    break;
                case "ping":
                    command = new PingCommand(this);
                    break;
                default:
                    // TODO: make self class exception
                    throw new Exception("Unknown command");
            }
//            command.parseArgs(jsonRootObject.getJSONObject("args"));
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
