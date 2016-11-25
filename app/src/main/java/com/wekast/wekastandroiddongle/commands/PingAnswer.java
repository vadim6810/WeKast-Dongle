package com.wekast.wekastandroiddongle.commands;

public class PingAnswer extends Answer {

    public PingAnswer() {
        setType("ping");
        add("message", "ok");
    }

}
