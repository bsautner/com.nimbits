/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.email;

import com.nimbits.client.model.Const;
import com.nimbits.server.task.TaskFactoryLocator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class MailHandlerServlet extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(MailHandlerServlet.class.getName());

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        log.info("Incoming Mail");
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        try {
            final MimeMessage message = new MimeMessage(session, req.getInputStream());

            final Address a[] = message.getFrom();
            for (Address address : a) {
                log.info(address.toString());
            }
            final String contentType = message.getContentType();
            log.info(contentType);

            final String inContent = getContent(message, contentType);
            log.info(inContent);
            if (a.length > 0) {
                final InternetAddress aa = (InternetAddress) a[0];
                final String fromAddress = aa.getAddress();
                TaskFactoryLocator.getInstance().startIncomingMailTask(fromAddress, inContent);

            }
        } catch (MessagingException e) {
            log.severe(req.getMethod());
        }
    }

    private static String getContent(final MimeMessage message, final String contentType)
            throws MessagingException, IOException {

        return (contentType.contains(Const.WORD_MULTI_PART)) ? getMultipartContent(message) : (String) message.getContent();

    }

    private static String getMultipartContent(MimeMessage message) throws MessagingException, IOException {
        log.info("Getting content from multipart message");

        final DataHandler dataHandler = message.getDataHandler();
        final DataSource dataSource = dataHandler.getDataSource();
        final MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
        final Part part = mimeMultipart.getBodyPart(0);
        log.info("count: " + mimeMultipart.getCount());

        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            final Part p = mimeMultipart.getBodyPart(i);
            log.info("Type" + i + p.getContentType());
        }
        return (String) part.getContent();
    }


}
