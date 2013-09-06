/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.mobile.server;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import com.nimbits.mobile.R;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by benjamin on 9/2/13.
 */

public class MainActivity extends Activity {
    private static final int PORT = 8080;
    private TextView hello;
    private MyHTTPD server;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        hello = (TextView) findViewById(R.id.hello);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView textIpaddr = (TextView) findViewById(R.id.ipaddr);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        textIpaddr.setText("Please access! http://" + formatedIpAddress + ":" + PORT);

        try {
            server = new MyHTTPD();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (server != null)
            server.stop();
    }

    private class MyHTTPD extends NanoHTTPD {
        public MyHTTPD() throws IOException {
            super(PORT);
        }

        @Override
        public Response serve(String uri, Method method, Map<String, String> headers,
                              Map<String, String> parms, Map<String, String> files) {
            final StringBuilder buf = new StringBuilder();

            for (Entry<String, String> kv : headers.entrySet()) {
                buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
            }

            for (Entry<String, String> kv : parms.entrySet()) {
                buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
            }
            buf.append(method.name() + "\n");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.v("TEST", buf.toString());
                }
            });

            final String html = "<html><head><head><body><h1>" + buf.toString() + "</h1></body></html>";
            return new Response(Response.Status.OK, MIME_HTML, html);
        }
    }
}