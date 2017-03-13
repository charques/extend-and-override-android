package com.example.jmontero.ingwrapper;

import android.graphics.Bitmap;

/**
 * Created by jmontero on 05/03/2017.
 */

public class PinSelectedEvent {
    private final int pinPosition;

    public PinSelectedEvent(int message) {
        this.pinPosition = message;
    }

    public int getMessage() {
        return pinPosition;
    }

}
