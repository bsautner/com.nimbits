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
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.timespan.TimespanModelFactory;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.service.value.ValueService;
import com.nimbits.cloudplatform.server.admin.common.ServerInfo;
import com.nimbits.cloudplatform.server.communication.email.EmailService;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 10/9/12
 * Time: 3:50 PM
 */
@Service("dumpTask")

public class DumpTask extends HttpServlet implements org.springframework.web.HttpRequestHandler{
    private ValueService valueService;
    private static final Logger log = Logger.getLogger(DumpTask.class.getName());
    private EmailService emailService;
    private ServerInfo serverInfoService;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        final String json =  request.getParameter(Parameters.entity.getText());
        final String sd =  request.getParameter(Parameters.sd.getText());
        final String ed =  request.getParameter(Parameters.ed.getText());
        final Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
        final long sl = Long.valueOf(sd);
        final long el = Long.valueOf(ed);


        final Timespan timespan = TimespanModelFactory.createTimespan(new Date(sl), new Date(el));

        try {
            final List<Value> values = ValueTransaction.getDataSegment(entity, timespan);

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


            final String m = serverInfoService.getFullServerURL(request) + "/service/blob?" +Parameters.blobkey.getText() + "=" + key.getKeyString();



            emailService.sendEmail(emailAddress,m, "Your extracted data for " + entity.getName().getValue() + " is ready");
        } catch (Exception e) {
            log.severe(e.getMessage());
        }


    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public EmailService getEmailService() {
        return emailService;
    }


    public void setServerInfoService(ServerInfo serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    public ServerInfo getServerInfoService() {
        return serverInfoService;
    }
}
