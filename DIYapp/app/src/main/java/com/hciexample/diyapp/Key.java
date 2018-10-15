package com.hciexample.diyapp;

import android.graphics.RectF;

/**
 * Created by ssaurel on 15/03/2018.
 */
public class Key {

    public int sound;
    public RectF rect;
    public boolean down;

    public Key(RectF rect, int sound) {
        this.sound = sound;
        this.rect = rect;
    }
}