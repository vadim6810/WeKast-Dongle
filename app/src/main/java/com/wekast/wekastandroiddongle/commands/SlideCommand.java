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
    private String animation;
    private String video;
    private String audio;

    @Override
    public Answer execute() {
        controller.getService().showSlide(slide, animation);
        return new SlideAnswer();
    }

    @Override
    public void parseArgs(JSONObject jsonObject) throws JSONException {
        slide = jsonObject.getString("slide");
        animation = jsonObject.getString("animation");
        video = jsonObject.getString("video");
        audio = jsonObject.getString("audio");
    }

    @Override
    public String getCommand() {
        return "slide";
    }

    public String getSlide() {
        return slide;
    }

    public String getAnimation() {
        return animation;
    }

    public String getVideo() {
        return video;
    }

    public String getAudio() {
        return audio;
    }

}
