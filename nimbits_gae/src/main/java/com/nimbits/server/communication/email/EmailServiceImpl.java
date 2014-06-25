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

package com.nimbits.server.communication.email;


import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.ServerInfo;
import com.nimbits.server.transaction.settings.SettingServiceFactory;
import com.nimbits.server.transaction.settings.SettingsService;

import javax.jdo.PersistenceManagerFactory;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

public class EmailServiceImpl implements EmailService {


    private static final Logger log = Logger.getLogger(EmailServiceImpl.class.getName());
    private static final String DEFAULT_EMAIL_SUBJECT = "Nimbits Messaging";
    private static final String WORD_NIMBITS = "Nimbits";
    private static final int INT = 128;
    private static final int SECONDS_IN_MINUTE = 60;
     private final PersistenceManagerFactory pmf;

    public EmailServiceImpl(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }


    private static void send(final Message msg) {

        try {
            Transport.send(msg);

        } catch (MessagingException e) {


        }

    }


    @Override
    public void sendEmail(final EmailAddress emailAddress, final String message) {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        log.info(emailAddress + " " + message);
        try {


            final InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            final Message msg = new MimeMessage(session);
            msg.setFrom(getFromEmail());
            msg.addRecipient(Message.RecipientType.TO, internetAddress);
            msg.setSubject(UserMessages.MESSAGE_EMAIL_SUBJECT);
            msg.setContent(message, Const.CONTENT_TYPE_HTML);
            send(msg);

        } catch (AddressException e) {

        } catch (MessagingException e) {

        } catch (UnsupportedEncodingException e) {

        }
    }




    @Override
    public void sendEmail(final EmailAddress emailAddress,
                          final String message,
                          final String subject) {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        try {
            log.info(emailAddress + " " + message);
            final InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            final Message msg = new MimeMessage(session);
            msg.setFrom(getFromEmail());
            msg.addRecipient(Message.RecipientType.TO, internetAddress);

            msg.setSubject(subject);
            msg.setContent(message, Const.CONTENT_TYPE_HTML);
            Transport.send(msg);

        } catch (AddressException e) {

        } catch (MessagingException e) {

        } catch (UnsupportedEncodingException e) {

        }
    }

    @Override
    public void sendEmail(final EmailAddress fromEmail,
                          final EmailAddress emailAddress,
                          final String message,
                          final String subject) {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        try {
            log.info(emailAddress + " " + message);
            final InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            final InternetAddress from = new InternetAddress(fromEmail.getValue());
            final Message msg = new MimeMessage(session);
            msg.setFrom(from);
            msg.addRecipient(Message.RecipientType.TO, internetAddress);

            msg.setSubject(subject);
            msg.setContent(message, Const.CONTENT_TYPE_HTML);
            Transport.send(msg);

        } catch (AddressException e) {

        } catch (MessagingException e) {

        }
    }
    private InternetAddress getFromEmail() throws UnsupportedEncodingException {
        final String fromEmail;
      fromEmail = SettingServiceFactory.getDaoInstance(pmf).getSetting(ServerSetting.admin );
            return new InternetAddress(fromEmail, WORD_NIMBITS);


    }


    @Override
    public void sendAlert(final Entity entity,
                          final Point point,
                          final EmailAddress emailAddress,
                          final Value value, Subscription subscription)  {

        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        final StringBuilder message = new StringBuilder(INT);
        message.append("<p>This is an alert email from <A HREF=www.nimbits.com>nimbits.com</A></p>")
                .append("<p>Data Point: ").append(entity.getName().getValue()).append("</p>");


        switch (value.getAlertState()) {
            case HighAlert: {
                message.append("<P>Alarm Status: High</P>")
                        .append("<P>Alarm Setting: ").append(point.getHighAlarm()).append("</P>")
                        .append("<p>Value Recorded: ").append(value.getDoubleValue()).append("</p>")
                        .append("<p>Note : ").append(value.getNote()).append("</p>");
                break;
            }
            case LowAlert: {
                message.append("<P>Alarm Status: Low</P>")
                        .append("<P>Alarm Setting: ").append(point.getLowAlarm()).append("</P>")
                        .append("<p>Value : ").append(value.getDoubleValue()).append("</p>")
                        .append("<p>Note : ").append(value.getNote()).append("</p>");
                break;
            }
            case IdleAlert: {
                message.append("<P>Alarm Status: Idle</P>")
                        .append("<P>Idle Setting: ").append(point.getIdleSeconds() / SECONDS_IN_MINUTE).append(" minutes</P>");
               break;
            }
            case OK: {
                message.append("<P>Alarm Status: OK</P>")
                .append("<p>Value : ").append(value.getDoubleValue()).append("</p>")
                .append("<p>Note : ").append(value.getNote()).append("</p>");

            }

        }


        message.append("<p></p>").append("<p><a href =\"").append(ServerInfo.getFullServerURL(null)).append("?uuid=").append(point.getUUID()).append("\">Go to Current Status Report</a></p>");

        message.append("<P>Subscription Name: ").append(subscription.getName().getValue()).append(" </P>");
        message.append("<P>Subscription Description: ").append(subscription.getDescription()).append(" </P>");

        try {

            final Message msg = new MimeMessage(session);
            final InternetAddress from = getFromEmail();
            if (from == null) {
                log.severe("Null email from sendAlert");
            } else
            {
            msg.setFrom(from);
            InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            msg.addRecipient(Message.RecipientType.TO, internetAddress);
            msg.setSubject(DEFAULT_EMAIL_SUBJECT);
            msg.setContent(message.toString(), Const.CONTENT_TYPE_HTML);
            log.info(emailAddress + " " + message);
            send(msg);
            log.info("to" + internetAddress.getAddress());
                log.info("from:" + from.getAddress());
            }
        } catch (AddressException e) {

        } catch (MessagingException e) {

        } catch (UnsupportedEncodingException e) {

        }
    }

    @Override
    public void setSettingService(SettingsService settingService) {

    }

    @Override
    public void sendConnectionRequest(User user, Connection c) {
        EmailAddress from = user.getEmail();
        EmailAddress to = CommonFactory.createEmailAddress(c.getTargetEmail());

        StringBuilder sb = new StringBuilder();
        sb
                .append("<P>" + user.getEmail().getValue() + " would like to connect nimbits accounts with you. </p>" +
                        "<P>Click here to approve: </P>")

                .append(ServerInfo.getFullServerURL(null))
                .append("service/v2/connection?key=")
                .append(c.getApprovalKey())

                .append("<p> This will give ")
                .append(from.getValue())
                .append(" permission to read all of your nimbits data.  Also, you'll be able to read ")
                .append("all of their nimbits objects. Only do this if you know and trust the sender. <p>")
                .append("<p>Learn more at http://www.nimbits.com/howto_connections.jsp</p>");
        ;



        String subject = "Nimbits Connection Request";
        sendEmail(to, sb.toString(), subject);

    }

    @Override
    public void sendConnectionRequestConfirmation(Connection c) {
        EmailAddress owner = CommonFactory.createEmailAddress(c.getOwner());
        EmailAddress to = CommonFactory.createEmailAddress(c.getTargetEmail());

        StringBuilder sb = new StringBuilder();
        sb
                .append("<P>" + to.getValue() + " has approved your connection request. You can now login to see their data points, and they can see yours." +  "</P>")

                .append(ServerInfo.getFullServerURL(null))



                .append("<p>Learn more at http://www.nimbits.com/howto_connections.jsp</p>");
        ;



        String subject = "Nimbits Connection Request Complete";
        sendEmail(owner, sb.toString(), subject);
    }


}
