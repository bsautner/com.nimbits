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

package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.common.collect.Range;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.ServerInfo;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.communication.email.EmailService;
import com.nimbits.server.communication.email.EmailServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.Date;
import java.util.List;


public class DumpTask extends ApiBase {


    //private EmailService emailService;
    //private ServerInfo serverInfoService;
    private ValueService valueService;
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        setup(request, response);
        valueService = ValueServiceFactory.getInstance(engine, taskService);
        final String json =  request.getParameter(Parameters.entity.getText());
        final String sd =  request.getParameter(Parameters.sd.getText());
        final String ed =  request.getParameter(Parameters.ed.getText());
        final Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
        final long sl = Long.valueOf(sd);
        final long el = Long.valueOf(ed);


        final Range timespan = Range.closed(new Date(sl), new Date(el));


            final List<Value> values = valueService.getDataSegment(entity, timespan);

            final FileService fileService = FileServiceFactory.getFileService();
            final AppEngineFile file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
            final String path = file.getFullPath();
            final FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
            final PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            for (final Value v : values) {
                out.println(v.getTimestamp().getTime() +  "," + v.getDoubleValue() + "," + v.getNote() + "," + v.getData() + "," + v.getLocation().getLat() + "," + v.getLocation().getLng());
            }


            out.close();
            writeChannel.closeFinally();
            final BlobKey key = fileService.getBlobKey(file);
            final EmailAddress emailAddress = CommonFactory.createEmailAddress(entity.getOwner());


            final String m = ServerInfo.getFullServerURL(request) + "/service/blob?" +Parameters.blobkey.getText() + "=" + key.getKeyString();


            EmailService emailService = EmailServiceFactory.getServiceInstance(engine);
            emailService.sendEmail(emailAddress,m, "Your extracted data for " + entity.getName().getValue() + " is ready");



    }


}
