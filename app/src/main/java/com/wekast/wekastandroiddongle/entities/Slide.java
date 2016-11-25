package com.wekast.wekastandroiddongle.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ELAD on 11/20/2016.
 */

public class Slide {
    private String title;
    private int slideNumber;
    private String comments;
    private String filePath;
    private ArrayList<Integer> chID = new ArrayList<>();
    private Map<Integer, String> mediaType = new HashMap<>();

    public Slide(String title, int slideNumber, String comments, String filePath, ArrayList<Integer> chID, Map<Integer, String> mediaType) {
        this.title = title;
        this.slideNumber = slideNumber;
        this.comments = comments;
        this.filePath = filePath;
        this.chID = chID;
        this.mediaType = mediaType;
    }

    public Map<Integer, String> getMediaType() { return mediaType; }

    public ArrayList<Integer> getChID() {
        return chID;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public int getSlideNumber() {
        return slideNumber;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "Slide{" +
                "title='" + title + '\'' +
                ", slideNumber=" + slideNumber +
                ", comments='" + comments + '\'' +
                ", filePath='" + filePath + '\'' +
                ", chID=" + chID +
                ", mediaType=" + mediaType +
                '}';
    }
}
