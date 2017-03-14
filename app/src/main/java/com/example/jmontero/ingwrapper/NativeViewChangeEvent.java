package com.example.jmontero.ingwrapper;

/**
 * Created by jmontero on 05/03/2017.
 */

public class NativeViewChangeEvent {

    public static final String NATIVE_CHANGE = "nativeChange";
    public static final String WEBVIEW_CHANGE ="webviewChange";
    private final String message;

    public NativeViewChangeEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
