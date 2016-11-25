package com.wekast.wekastandroiddongle.commands;

import com.wekast.wekastandroiddongle.controllers.CommandController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ELAD on 10/23/2016.
 */
public class SlideCommand implements ICommand {

    private CommandController controller;

    public SlideCommand(CommandController controller) {
        this.controller = controller;
    }

    private String slide;
    private String media;

    @Override
    public Answer execute() {
        controller.getService().showSlide(slide, media);
        return new SlideAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {
        slide = jsonObject.getString("slide");
        media = jsonObject.getString("media");
    }

    @Override
    public String getCommand() {
        return "slide";
    }

}
