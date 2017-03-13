package com.example.jmontero.ingwrapper;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.greenrobot.event.EventBus;

/**
 * Created by jmontero on 04/03/2017.
 */

public class FragmentWebView extends Fragment {

    private WebView webview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = initFragment(inflater, container);
        initEventBus();
        initWebView(view);

        loadUrl("https://ing.ingdirect.es/pfm/#login/sso");

        return view;
    }

    @NonNull
    private View initFragment(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.webview, container, false);
    }

    private void initWebView(View view) {
        webview = (WebView) view.findViewById(R.id.webpage);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
    }

    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void loadUrl(String url) {
        webview.loadUrl(url);
    }

    public void onEvent(WebNavigationEvent event){
        loadUrl(event.getMessage());
    }
}
