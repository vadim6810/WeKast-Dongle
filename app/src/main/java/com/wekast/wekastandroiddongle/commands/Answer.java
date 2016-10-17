package com.wekast.wekastandroiddongle.commands;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class Answer extends JSONObject {
    Answer() {
        add("device", "dongle");
    }

    void add(String name, Object value) {
        try {
            put(name, value);
        } catch (JSONException ignored) {}
    }

    void setType(String type) {
        add("type", type);
    }
}
