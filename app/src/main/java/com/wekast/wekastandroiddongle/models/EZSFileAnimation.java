package com.wekast.wekastandroiddongle.models;

import java.io.File;

/**
 * Created by ELAD on 9/17/2016.
 */
public class EZSFileAnimation {

    File animation;

    public EZSFileAnimation(File file) {
        this.animation = file;
    }

    public File getAnimation() {
        return this.animation;
    }
}
