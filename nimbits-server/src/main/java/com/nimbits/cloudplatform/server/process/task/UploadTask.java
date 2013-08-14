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

package com.nimbits.cloudplatform.server.process.task;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.location.Location;
import com.nimbits.cloudplatform.client.model.location.LocationFactory;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModel;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.ValueData;
import com.nimbits.cloudplatform.client.model.value.impl.ValueDataModel;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 10/9/12
 * Time: 3:50 PM
 */
@Service("uploadTask")

public class UploadTask extends HttpServlet implements org.springframework.web.HttpRequestHandler{




    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String key = request.getParameter(Parameters.blobkey.getText());
        final String json = request.getParameter(Parameters.entity.getText());
        final String userJson= request.getParameter(Parameters.user.getText());
        final BlobKey blobKey = new BlobKey(key);
        final Point entity = GsonFactory.getInstance().fromJson(json, PointModel.class);
        final User user = GsonFactory.getInstance().fromJson(userJson, UserModel.class);
        FileService fileService = FileServiceFactory.getFileService();
        AppEngineFile uFile = fileService.getBlobFile(blobKey);
        FileReadChannel readChannel = fileService.openReadChannel(uFile, false);

        // Again, different standard Java ways of reading from the channel.
        BufferedReader reader =
                new BufferedReader(Channels.newReader(readChannel, "UTF8"));
        String line;

        try {
        List<Value> values = new ArrayList<Value>(100);
        while ((line = reader.readLine()) != null) { // while loop begins here
            Value v = processString(line);

            if (v != null) {
                values.add(v);
            }

        }

        ValueTransaction.recordValues(user, entity, values);

        } catch (Exception e) {
           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           response.setHeader("ERROR", e.getMessage());

        }  finally {
            readChannel.close();
        }

    }
    private Value processString(String line) throws Exception {
        Date date = null;
        double value = 0;
        String note = null;
        String data = null;
        double lat = 0;
        double lng = 0;
        try {
            String[] split = line.split(",");
            if (split.length >= 2) {

                date = new Date(Long.valueOf(split[0]));
                value = Double.valueOf(split[1]);

            }
            if (split.length >= 3) {
                note = split[2];
            }
            if (split.length >= 4) {
                data = split[3];
            }
            if (split.length >= 5) {
                lat = Double.valueOf(split[4]);
            }
            if (split.length >= 6) {
                lng = Double.valueOf(split[5]);
            }
            Location location = LocationFactory.createLocation(lat, lng);
            ValueData valueData = ValueDataModel.getInstance(SimpleValue.getInstance(data));
            if (date == null) {
                throw new Exception("Uploaded Data Must contain a timestamp in unix epoch format in Milliseconds as the first column in your CSV file." );
            }
            return ValueFactory.createValueModel(location, value, date, note, valueData, AlertType.OK);
        } catch (NumberFormatException e) {
            return null;
        }

    }

}
