package com.kc9zyz.cc3200wifiledblinky;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.IOException;
import java.net.InetAddress;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText mEdit   = (EditText)findViewById(R.id.editText);
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format(
                "%d.%d.%d.",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff));
        mEdit.setText(ipString);
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
    public void scanIP(View view)
    {
        int ipSuffix = 0;
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format(
                "%d.%d.%d.",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff));

        HttpClient client = new DefaultHttpClient();
         while(ipSuffix < 256) {
             try {
                 HttpPost post = new HttpPost(ipString + ipSuffix);
                 HttpResponse response = client.execute(post);

             } catch (IOException e) {
                 ipSuffix++;
             }
         }
    }
}
