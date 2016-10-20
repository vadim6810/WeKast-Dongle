package com.wekast.wekastandroiddongle.commands;

        import org.json.JSONArray;
        import org.json.JSONException;
/**
 * Created by ELAD on 10/15/2016.
 */

public interface ICommand {
    Answer execute();
    void parseArgs(JSONArray args) throws JSONException;
}
