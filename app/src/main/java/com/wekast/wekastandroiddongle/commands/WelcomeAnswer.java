package com.wekast.wekastandroiddongle.commands;


public class WelcomeAnswer extends Answer {
    public WelcomeAnswer() {
        super();
        setType("message");
        add("message", "Welcome to dongle");
    }
}
