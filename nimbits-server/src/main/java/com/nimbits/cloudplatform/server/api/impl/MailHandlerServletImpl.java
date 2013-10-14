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

package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.constants.Words;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;


@Service("mailhandler")
public class MailHandlerServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private TaskImpl TaskImpl;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        try {
            doInit(req, resp, ExportType.unknown);
            final Properties props = new Properties();
            final Session session = Session.getDefaultInstance(props, null);
            final MimeMessage message = new MimeMessage(session, req.getInputStream());

            final Address a[] = message.getFrom();

            final String contentType = message.getContentType();

            final String inContent = getContent(message, contentType);

            if (a.length > 0) {
                final InternetAddress aa = (InternetAddress) a[0];
                final String fromAddress = aa.getAddress();
                TaskImpl.startIncomingMailTask(fromAddress, inContent);

            }
        } catch (MessagingException e) {
            LogHelper.logException(this.getClass(), e);
        } catch (Exception e) {
            LogHelper.logException(this.getClass(), e);
        }
    }

    private static String getContent(final MimeMessage message, final String contentType)
            throws MessagingException, IOException {

        return contentType.contains(Words.WORD_MULTI_PART) ? getMultipartContent(message) : (String) message.getContent();

    }

    private static String getMultipartContent(MimeMessage message) throws MessagingException, IOException {

        final DataHandler dataHandler = message.getDataHandler();
        final DataSource dataSource = dataHandler.getDataSource();
        final MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
        final Part part = mimeMultipart.getBodyPart(0);

//        for (int i = 0; i < mimeMultipart.getCount(); i++) {
//            final Part p = mimeMultipart.getBodyPart(i);
//
//        }
        return (String) part.getContent();
    }


    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (isPost(req)) {

            doPost(req, resp);
        }
        else {
            doGet(req, resp);
        }
    }

    public void setTaskImpl(TaskImpl TaskImpl) {
        this.TaskImpl = TaskImpl;
    }

    public TaskImpl getTaskImpl() {
        return TaskImpl;
    }
}
