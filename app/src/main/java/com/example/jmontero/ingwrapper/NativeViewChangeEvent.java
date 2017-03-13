package com.example.jmontero.ingwrapper;

/**
 * Created by jmontero on 05/03/2017.
 */

public class NativeViewChangeEvent {
    private final String message;

    public NativeViewChangeEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
