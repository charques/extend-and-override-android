package com.example.jmontero.ingwrapper;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Iterator;

import de.greenrobot.event.EventBus;

/**
 * Created by jmontero on 04/03/2017.
 */

class MyWebViewClient extends WebViewClient {

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Uri uri = Uri.parse(url);
        return handleUri(view, uri);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        if (request.getUrl().getPath().contains("genoma_api")) {
            final Iterator<String> cookiesIt = request.getRequestHeaders().keySet().iterator();
            while (cookiesIt.hasNext())
                Log.d("MyApplication", "url: " + request.getUrl() +  " Cookie= " + cookiesIt.next());
        }

        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    private boolean handleUri(final WebView view, final Uri uri) {

        final String host = uri.getHost();

        if ("www.ingdirect.es".equals(host)) {
            // Returning false means that you are going to load this url in the webView itself
            Log.d("MyApplication", "Pasar a navegación nativa");
            EventBus.getDefault().post(new NativeViewChangeEvent("nativeChange"));
            return true;
        } else {
            // Returning true means that you need to handle what to do with the url
            // e.g. open web page in a Browser
            Log.d("MyApplication", "Continuar con navegación web");

        }
        return false;
    }
}
