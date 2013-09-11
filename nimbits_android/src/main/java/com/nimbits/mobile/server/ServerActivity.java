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
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.JsonSyntaxException;
import com.nimbits.cloudplatform.client.model.value.ValueContainer;
import com.nimbits.cloudplatform.client.model.value.impl.ValueContainerModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory2;
import com.nimbits.mobile.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ServerActivity extends Activity {
    private static final int PORT = 8080;
    public static final String TAG = "BufferService";
    private IncomingValueBroadCastReciever reciever;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity_layout);

        ListView list = (ListView) findViewById(R.id.listView);

        try {
            new HttpServer().start();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }


        List<ValueContainer> buffered = new ArrayList<ValueContainer>();
        File root = HttpServer.getRootDir();
        if (root.list() != null) {
            for (String s : root.list()) {
                Log.v(TAG, s);
                File file = new File(root,s);
                StringBuilder text = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    ValueContainer c;
                    try {
                        c = GsonFactory2.getInstance().fromJson(text.toString(), ValueContainerModel.class);
                    }
                    catch (JsonSyntaxException ex ) {
                        c = null;
                    }
                    if (c != null) {
                        buffered.add(c);
                    }
                    else {
                        file.delete();
                    }



                }

                catch (IOException e) {
                    file.delete();
                    Log.e(TAG, e.getMessage());
                }

            }
        }
        ValueListAdapter adapter = new ValueListAdapter(this, R.id.listView,buffered);

        list.setAdapter(adapter);
        if (reciever == null) {
            IntentFilter filter = new IntentFilter(IncomingValueBroadCastReciever.ACTION);
            reciever = new IncomingValueBroadCastReciever();
            this.registerReceiver(reciever, filter);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView textIpaddr = (TextView) findViewById(R.id.ipaddr);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        textIpaddr.setText("Server is running on http://" + formatedIpAddress + ":" + PORT);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
    }
}