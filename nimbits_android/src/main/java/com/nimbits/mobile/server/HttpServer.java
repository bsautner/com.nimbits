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

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.nimbits.cloudplatform.client.model.value.ValueContainer;
import com.nimbits.cloudplatform.client.model.value.impl.ValueContainerModel;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.gson.GsonFactory2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


public class HttpServer extends NanoHTTPD {
    private static final int PORT = 8080;
    public final static String TAG = "BufferService";
    public HttpServer() {
        super(PORT);
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers,
                          Map<String, String> parms, Map<String, String> files) {

        Log.v(TAG, uri);
        if (uri.equals("/service/v2/value")) {

            processValue(parms);
            return new Response(Response.Status.OK, MIME_HTML, "value buffered on device ");


        }
        else {
            return new Response(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "You must post values as valid json");
        }

    }

    protected void processValue(Map<String, String> parms)   {
        String json = parms.get("NanoHttpd.QUERY_STRING");
        ValueContainer valueContainer = null;
        try {
            valueContainer = GsonFactory2.getInstance().fromJson(json, ValueContainerModel.class);
        }
        catch (Exception ex) {
            Log.v(TAG, ex.getMessage());

        }
        String state = Environment.getExternalStorageState();

        if (valueContainer != null && Environment.MEDIA_MOUNTED.equals(state)) {
            if (valueContainer.getValue().getTimestamp().getTime() == 0) {
                valueContainer.setValue(ValueFactory.createValueModel(valueContainer.getValue(), new Date()));
            }
            String FILENAME = UUID.randomUUID().toString();//valueContainer.getId() + "." + valueContainer.getValue().getTimestamp().getTime();

            File file = new File(getRootDir(), FILENAME);

            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream f = new FileOutputStream(file);
                f.write(json.getBytes());
                f.close();
            }
            catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
            Intent intent = new Intent();
            intent.setAction(IncomingValueBroadCastReciever.ACTION);
            // Bundle bundle = new Bundle();
            // bundle.putSerializable("DATA", valueContainer);
            // sendBroadcast(intent);

        }


    }
    public static File getRootDir( ) {
        File sdcard = Environment.getExternalStorageDirectory();
        String root = sdcard + "/.nimbits";
        File rootDir = new File(root);
        if (! rootDir.exists()) {
            rootDir.mkdirs();
        }
        return rootDir;
    }
}

