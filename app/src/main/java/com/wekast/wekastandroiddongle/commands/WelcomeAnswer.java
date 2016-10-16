package com.wekast.wekastandroiddongle.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Samanta on 17.10.2016.
 */

public class WelcomeAnswer extends JSONObject {
    public WelcomeAnswer() {
        try {
            put("device", "dongle");
            put("message", "Welcome to dongle");
        } catch (JSONException ignored) {}
    }
}
