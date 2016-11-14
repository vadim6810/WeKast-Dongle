package com.wekast.wekastandroiddongle.commands;

/**
 * Created by ELAD on 11/14/2016.
 */

public class PingAnswer extends Answer {
    public PingAnswer() {
        setType("ping");
        add("message", "ok");
    }
}
