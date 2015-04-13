package com.kc9zyz.cc3200wifiledblinky;

import android.annotation.TargetApi;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.config.RequestConfig;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private String deviceURI = "http://192.168.1.93";
    private TextView mText;
    private TextView mText1;
    private TextView mText2;

    private ToggleButton redToggleVar;
    private ToggleButton greenToggleVar;
    private HttpClient client;

    public void initHttpClient() {
        client = new DefaultHttpClient();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.textView4);
        mText1 = (TextView) findViewById(R.id.textView2);
        mText2 = (TextView) findViewById(R.id.textView3);
        redToggleVar = (ToggleButton) findViewById(R.id.redToggle);
        greenToggleVar = (ToggleButton) findViewById(R.id.greenToggle);
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format(
                "%d.%d.%d.",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff));
        mText.setText(ipString);
        initHttpClient();

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

        return super.onOptionsItemSelected(item);
    }

    public void scanIP(View view) {
        int ipSuffix = 93;
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format(
                "%d.%d.%d.",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff));

        while (ipSuffix < 256) {
            try {
                HttpGet get = new HttpGet("http://" + ipString + ipSuffix + "/led_demo.html");
                // HttpGet get = new HttpGet("http://192.168.1.93/led_demo.html");
                RequestConfig Default = RequestConfig.DEFAULT;
                RequestConfig requestConfig = RequestConfig.copy(Default)
                        .setSocketTimeout(50)
                        .setConnectTimeout(50)
                        .setConnectionRequestTimeout(50)
                        .build();
                get.setConfig(requestConfig);
                
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    deviceURI = "http://" + ipString + ipSuffix;
                    mText.setText(deviceURI);
                    mText1.setEnabled(true);
                    mText2.setEnabled(true);
                    redToggleVar.setEnabled(true);
                    greenToggleVar.setEnabled(true);
                    break;
                } else
                    ipSuffix++;

            } catch (IOException e) {
                ipSuffix++;
            }
        }
    }
    public void greenToggleClick(View view)
    {
        postHTTP(2, greenToggleVar.isChecked());
    }
    public void redToggleClick(View view)
    {
        postHTTP(1, redToggleVar.isChecked());
    }
    private void postHTTP(int ledNum, boolean isOn)
    {
        HttpPost post = new HttpPost(deviceURI);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if(isOn)
            pairs.add(new BasicNameValuePair("__SL_P_ULD", "LED"+ledNum+"_ON"));
        else
            pairs.add(new BasicNameValuePair("__SL_P_ULD", "LED"+ledNum+"_OFF"));
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
