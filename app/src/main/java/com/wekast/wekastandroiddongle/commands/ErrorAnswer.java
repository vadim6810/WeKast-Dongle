package com.wekast.wekastandroiddongle.commands;


public class ErrorAnswer extends Answer {
    public ErrorAnswer(Exception e) {
        setType("error");
        add("message", e.getMessage());
    }
}
