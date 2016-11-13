package com.wekast.wekastandroiddongle.commands;

/**
 * Created by ELAD on 11/12/2016.
 */

public class StopAnswer extends Answer {
    public StopAnswer() {
        setType("stop");
        add("message", "ok");
    }
}
