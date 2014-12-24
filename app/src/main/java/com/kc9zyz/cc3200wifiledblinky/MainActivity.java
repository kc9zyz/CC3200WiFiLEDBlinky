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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private String deviceURI;
    private TextView mText;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.textView4);
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format(
                "%d.%d.%d.",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff));
        mText.setText(ipString);
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
        int ipSuffix = 91;
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format(
                "%d.%d.%d.",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff));

        HttpClient client = new DefaultHttpClient();
        while (ipSuffix < 256) {
            try {
                HttpGet get = new HttpGet("http://" + ipString + ipSuffix + "/led_demo.html");
                // HttpGet get = new HttpGet("http://192.168.1.93/led_demo.html");
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    deviceURI = "http://" + ipString + ipSuffix;
                    mText.setText(deviceURI);
                    break;
                } else
                    ipSuffix++;


            } catch (IOException e) {
                ipSuffix++;
            }
        }
    }
}
