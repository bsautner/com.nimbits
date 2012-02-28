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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.email;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.common.*;
import com.nimbits.server.settings.*;
import org.apache.commons.lang3.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class EmailServiceImpl implements EmailService {

    private static final Logger log = Logger.getLogger(EmailServiceImpl.class.getName());



    private void send(final Message msg) {

        try {
            Transport.send(msg);

        } catch (MessagingException e) {
            log.severe(e.getMessage());

        }

    }

    public void sendEmail(final EmailAddress emailAddress, final String message) throws NimbitsException {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);

        try {
            InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            final Message msg = new MimeMessage(session);
            msg.setFrom(getFromEmail());
            msg.addRecipient(Message.RecipientType.TO, internetAddress);
            msg.setSubject(Const.MESSAGE_EMAIL_SUBJECT);
            msg.setContent(message, Const.CONTENT_TYPE_HTML);
            send(msg);

        } catch (AddressException e) {
            log.severe(e.getMessage());
        } catch (MessagingException e) {
            log.severe(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.severe(e.getMessage());
        }
    }

    public void sendEmail(final EmailAddress emailAddress,
                          final String message,
                          final String subject) throws NimbitsException {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        try {
            final InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            final Message msg = new MimeMessage(session);
            msg.setFrom(getFromEmail());
            msg.addRecipient(Message.RecipientType.TO, internetAddress);

            msg.setSubject(subject);
            msg.setContent(message, Const.CONTENT_TYPE_HTML);
            Transport.send(msg);

        } catch (AddressException e) {
            log.severe(e.getMessage());
        } catch (MessagingException e) {
            log.severe(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.severe(e.getMessage());
        }
    }

    private static InternetAddress getFromEmail() throws UnsupportedEncodingException {
        final String fromEmail;
        try {
            fromEmail = new SettingServiceImpl().getSetting(Const.PARAM_ADMIN);
            return new InternetAddress(fromEmail, Const.WORD_NIMBITS);
        } catch (NimbitsException e) {
          return  new InternetAddress(Const.TEST_ACCOUNT, Const.WORD_NIMBITS);
        }

    }

    public void sendAlert(final Entity entity,
                          final Point point,
                          final EmailAddress emailAddress,
                          final Value value) {

        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        final StringBuilder message = new StringBuilder();
        message.append("<p>This is an alert email from <A HREF=www.nimbits.com>Nimbits Data Logger</A></p>")
                .append("<p>Data Point: ").append(entity.getName().getValue()).append("</p>");


        switch (value.getAlertState()) {
            case HighAlert: {
                message.append("<P>Alarm Status: High</P>")
                        .append("<P>Alarm Setting: ").append(point.getHighAlarm()).append("</P>")
                        .append("<p>Value Recorded: ").append(value.getNumberValue()).append("</p>");
                break;
            }
            case LowAlert: {
                message.append("<P>Alarm Status: Low</P>")
                        .append("<P>Alarm Setting: ").append(point.getLowAlarm()).append("</P>")
                        .append("<p>Value Recorded: ").append(value.getNumberValue()).append("</p>");
                break;
            }
            case IdleAlert: {
                message.append("<P>Alarm Status: Idle</P>")
                        .append("<P>Idle Setting: ").append(point.getIdleSeconds() / 60).append(" minutes</P>");

            }
            case OK: {
                message.append("<P>Alarm Status: OK</P>");
            }

        }


        message.append("<p></p>")
                .append("<p><a href =\"" +
                        ServerInfoImpl.getFullServerURL(null) +
                        "?uuid=" +
                        point.getUUID() +
                        "\">Go to Current Status Report</a></p>");




        try {

            final Message msg = new MimeMessage(session);
            InternetAddress from = getFromEmail();
            if (from != null) {
            msg.setFrom(getFromEmail());
            InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            msg.addRecipient(Message.RecipientType.TO, internetAddress);
            msg.setSubject(Const.DEFAULT_EMAIL_SUBJECT);
            msg.setContent(message.toString(), Const.CONTENT_TYPE_HTML);
            send(msg);
            }
            else {
                log.severe("Null email from sendAlert");
            }
        } catch (AddressException e) {
            log.severe(e.getMessage());
        } catch (MessagingException e) {
            log.severe(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.severe(e.getMessage());
        }
    }
}
