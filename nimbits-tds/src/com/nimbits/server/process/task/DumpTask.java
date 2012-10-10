/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactoryImpl;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.common.ServerInfo;
import com.nimbits.server.communication.email.EmailServiceImpl;
import com.nimbits.server.gson.GsonFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class DumpTask extends HttpServlet implements org.springframework.web.HttpRequestHandler{
    private ValueService valueService;
    private static final Logger log = Logger.getLogger(DumpTask.class.getName());
    private EmailServiceImpl emailService;
    private CommonFactoryImpl commonFactory;
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
            final EmailAddress emailAddress = commonFactory.createEmailAddress(entity.getOwner());


            final String m = serverInfoService.getFullServerURL(request) + "/service/blob?" +Parameters.blobkey.getText() + "=" + key.getKeyString();



            emailService.sendEmail(emailAddress,m, "Your extracted data for " + entity.getName().getValue() + " is ready");
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }


    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }

    public void setEmailService(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    public EmailServiceImpl getEmailService() {
        return emailService;
    }

    public void setCommonFactory(CommonFactoryImpl commonFactory) {
        this.commonFactory = commonFactory;
    }

    public CommonFactoryImpl getCommonFactory() {
        return commonFactory;
    }

    public void setServerInfoService(ServerInfo serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    public ServerInfo getServerInfoService() {
        return serverInfoService;
    }
}
