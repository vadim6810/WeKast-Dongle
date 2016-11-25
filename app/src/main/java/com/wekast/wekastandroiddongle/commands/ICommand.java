package com.wekast.wekastandroiddongle.commands;

import org.json.JSONException;
import org.json.JSONObject;

public interface ICommand {

    Answer execute();
    void parseArgs(JSONObject jsonObject) throws JSONException;
    String getCommand();

}
