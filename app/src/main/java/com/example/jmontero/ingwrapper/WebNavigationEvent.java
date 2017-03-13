package com.example.jmontero.ingwrapper;

/**
 * Created by jmontero on 05/03/2017.
 */

public class WebNavigationEvent {
    private final String message;

    public WebNavigationEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
