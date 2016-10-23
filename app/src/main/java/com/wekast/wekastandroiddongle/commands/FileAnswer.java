package com.wekast.wekastandroiddongle.commands;

/**
 * Created by ELAD on 10/23/2016.
 */

public class FileAnswer extends Answer {
    public FileAnswer() {
        setType("file");
        add("message", "ok");
        add("port", "9999");
    }
}
