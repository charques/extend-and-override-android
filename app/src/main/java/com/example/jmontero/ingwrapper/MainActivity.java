package com.example.jmontero.ingwrapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    public static final String LOGIN_SESSION_URL = "https://ing.ingdirect.es/genoma_login/rest/session";

    private final HttpsClientTool httpsClientTool = new HttpsClientTool(this);
    private ViewSwitcher switcher;
    private List<Integer> pins = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initBusEvents();
        initToolbar();
        initViewSwitcher();
        initFavButton();
        initAcceptButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.movemoney) {
            EventBus.getDefault().post(new WebNavigationEvent("https://ing.ingdirect.es/pfm/#operation-handler/"));
            return true;
        }
        if (id == R.id.personalarea) {
            EventBus.getDefault().post(new WebNavigationEvent("https://ing.ingdirect.es/pfm/#personal-area/"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent(NativeViewChangeEvent event){
        switcher.showNext();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void initBusEvents() {
        EventBus myEventBus = EventBus.getDefault();
        myEventBus.register(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViewSwitcher() {
        switcher = (ViewSwitcher) findViewById(R.id.container);
        switcher.showNext();
    }

    private void initFavButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentWebView wvf = new FragmentWebView();

                getFragmentManager().beginTransaction()
                        .add(R.id.webcontainer, wvf).commit();
                switcher.showNext();
            }
        });
    }

    private void initAcceptButton() {
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    final String result = doRequest(createLoginJson(),
                            new URL(LOGIN_SESSION_URL), "POST", "application/json");

                    showPinPad(result);
                    showPositionsNeeded(result);

                } catch (JSONException | IOException e) {
                    Log.e("MyApplication", e.getMessage());
                }
            }
        });
    }

    @NonNull
    private String createLoginJson() throws JSONException {
        final EditText dni = (EditText) findViewById(R.id.dniText);
        final EditText birthday = (EditText) findViewById(R.id.birthDateText);
        JSONObject session = new JSONObject("{}");
        session.put("birthday", birthday.getText());
        session.put("device", "desktop");
        JSONObject loginDocument = new JSONObject("{}");
        loginDocument.put("document", dni.getText());
        loginDocument.put("documentType", 0);
        session.put("loginDocument", loginDocument);
        return session.toString();
    }

    private String doRequest(String session, URL url, String method, String contentType) {
        return httpsClientTool.doRequest(session, url, method, contentType);
    }

    private void showPinPad(String result) throws JSONException, IOException {
        getImage(result, R.id.pin0, 0);
        getImage(result, R.id.pin1, 1);
        getImage(result, R.id.pin2, 2);
        getImage(result, R.id.pin3, 3);
        getImage(result, R.id.pin4, 4);
        getImage(result, R.id.pin5, 5);
        getImage(result, R.id.pin6, 6);
        getImage(result, R.id.pin7, 7);
        getImage(result, R.id.pin8, 8);
        getImage(result, R.id.pin9, 9);
    }

    private void showPositionsNeeded(String result) throws JSONException {
        JSONObject jsonResponse = new JSONObject(result);
        JSONArray positions = (JSONArray) jsonResponse.get("pinPositions");

        TextView positionsView = (TextView) findViewById(R.id.positions);
        positionsView.setText(new StringBuilder()
                .append(" Posiciones: ")
                .append(positions.toString()).toString());
    }

    private void getImage(final String inputLine, final @IdRes int viewId, final int index) throws JSONException, IOException {
        JSONObject result = new JSONObject(inputLine);
        byte[] byteArray = Base64.decode(result.getJSONArray("pinpad").getString(index), Base64.DEFAULT);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ImageView image = (ImageView) findViewById(viewId);

        image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                pins.add(index);
                if (pins.size() == 3) {
                    Log.d("MyApplication", "Seleccionados " + pins.get(0) + " "  + pins.get(1) + " "  + pins.get(2) + " ");

                    try {

                        JSONObject jsonResponse = sendPinPositions();
                        sendLoginToAPI(jsonResponse);
                        pins.clear();

                    } catch (JSONException | MalformedURLException | UnsupportedEncodingException e) {
                        Log.d("MyApplication", e.getMessage());
                    }
                }
            }

            private void sendLoginToAPI(JSONObject jsonResponse) throws JSONException, UnsupportedEncodingException, MalformedURLException {
                Map<String,Object> params = new LinkedHashMap<>();
                params.put("ticket", jsonResponse.get("ticket"));
                params.put("device", "desktop");

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                doRequest(postData.toString(),
                        new URL("https://ing.ingdirect.es/genoma_api/login/auth/response"),
                        "POST",
                        "application/x-www-form-urlencoded");
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @NonNull
            private JSONObject sendPinPositions() throws JSONException, MalformedURLException {
                JSONObject jsonRequest = new JSONObject("{}");

                jsonRequest.put("pinPositions", new JSONArray(pins.toArray()));

                String ticketResponse = doRequest(jsonRequest.toString(), new URL(LOGIN_SESSION_URL), "PUT", "application/json");

                Log.d("MyApplication", ticketResponse);
                return new JSONObject(ticketResponse);
            }
        });

        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                image.getHeight(), false));
    }

}
