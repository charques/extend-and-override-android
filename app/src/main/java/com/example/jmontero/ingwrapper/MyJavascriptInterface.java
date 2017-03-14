package com.example.jmontero.ingwrapper;

import de.greenrobot.event.EventBus;

/**
 * Created by jmontero on 13/03/2017.
 */

public class MyJavascriptInterface {
    @android.webkit.JavascriptInterface
    public void listenHashChange(){
        EventBus.getDefault().post(new NativeViewChangeEvent("webviewChange"));
    }
}
