package com.example.jmontero.ingwrapper;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HttpsClientTool {

    public static final String SET_COOKIE = "Set-Cookie";

    private CookieManager cookieManager;
    private CookieSyncManager cookieSyncManager;

    public HttpsClientTool(MainActivity mainActivity) {
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieSyncManager = CookieSyncManager.createInstance(mainActivity);
    }

    String doRequest(String session, URL url, String method, String contentType) {
        HttpsURLConnection conn = null;
        StringBuffer inputLine = new StringBuffer();
        try {
            conn = (HttpsURLConnection) url.openConnection();
            // Create the SSL connection
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());

            // set Timeout and method
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", contentType);

            loadCookieToRequest(conn);

            //conn.connect();
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(session.getBytes());
            out.flush();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(in));
            String tmp;
            Log.d("MyApplication", url.toString() + "Status Code: " + conn.getResponseCode());
            while ((tmp = buf.readLine()) != null) {
                inputLine.append(tmp);
            }
            buf.close();
            //use inputLine.toString(); here it would have whole source
            Log.d("MyApplication", inputLine.toString());
            saveCookies(conn);

        } catch (KeyManagementException | IOException | NoSuchAlgorithmException e) {
            Log.e("MyApplication", e.getMessage());
        } finally {
            conn.disconnect();
        }
        return inputLine.toString();
    }

    public void resetCookies() {
        cookieManager.removeAllCookie();
    }

    private void loadCookieToRequest(HttpsURLConnection conn) {
        if (cookieManager.hasCookies()) {
            // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
            conn.setRequestProperty("Cookie", "genoma-session-id=" +
                    cookieManager.getCookie("genoma-session-id"));
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveCookies(HttpsURLConnection conn) {
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(SET_COOKIE);
        if (cookiesHeader != null) {
            for (String cookieStringToParse : cookiesHeader) {
                final HttpCookie cookie = HttpCookie.parse(cookieStringToParse).get(0);
                Log.d("MyApplication", "Cookie from response= " + cookieStringToParse);
                cookieManager.setCookie("https://ing.ingdirect.es", cookieStringToParse, new ValueCallback<Boolean>() {
                    @Override
                    public void onReceiveValue(Boolean value) {
                        Log.d("MyApplication", value.toString());
                    }
                });//setCookie("https://ing.ingdirect.es", cookieStringToParse);
            }
        }
    }
}