/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.communication.mail;


import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueContainer;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.system.ServerInfo;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

@Service
public class EmailService {



    private ServerInfo serverInfo;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class.getName());

    private static final int INT = 128;
    private static final int SECONDS_IN_MINUTE = 60;

    @org.springframework.beans.factory.annotation.Value("${mail.smtp.host}")
    private String smtpServer;

    @org.springframework.beans.factory.annotation.Value("${mail.smtp.port}")
    private String smtpPort;

    @org.springframework.beans.factory.annotation.Value("${mail.smtp.password}")
    private String smtpPassword;

    @org.springframework.beans.factory.annotation.Value("${mail.smtp.from}")
    private String from;

    @org.springframework.beans.factory.annotation.Value("${mail.smtp.domain}")
    private String domain;

    @org.springframework.beans.factory.annotation.Value("${mail.smtp.subject}")
    private String subject;

    @Autowired
    public EmailService(ServerInfo serverInfo) {

        this.serverInfo = serverInfo;

    }

    private void send(final Message msg) {


        try {
            Transport transport = getMailTransport();

            if (transport != null) {
                transport.connect(smtpServer, from, smtpPassword);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
            } else {
                Transport.send(msg); //send statically for GAE
            }

        } catch (MessagingException e) {
            log.error(e.getMessage());

        }


    }

    private Transport getMailTransport() throws NoSuchProviderException {


        Properties props = new Properties();

        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.user", from);

        String AUTH = "true";
        props.put("mail.smtp.auth", AUTH);
        String STARTTLS = "true";
        props.put("mail.smtp.starttls.enable", STARTTLS);
        String DEBUG = "true";
        props.put("mail.smtp.debug", DEBUG);

        props.put("mail.smtp.socketFactory.port", smtpPort);
        String SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");


        //Obtain the default mail session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);


        return session.getTransport("smtps");

    }


    private String createEmailFromTemplate(String message) throws IOException {


        ClassLoader classLoader = getClass().getClassLoader();

        String html = IOUtils.toString(classLoader.getResourceAsStream("mail.html"));
        html = html.replace("MESSAGE_BODY_INSERTED", message);
        html = html.replace("INSTANCE_URL", serverInfo.getFullServerURL(null));
        html = html.replace("INSTANCE_NAME", serverInfo.getFullServerURL(null));
        return html;
    }


    private void sendEmail(final EmailAddress emailAddress,
                           final String unformattedMessage,
                           final String subject, boolean format) throws Exception {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);


            final String content = format ? createEmailFromTemplate(unformattedMessage) : unformattedMessage;

        final InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
        final Message msg = new MimeMessage(session);
        msg.setFrom(getFromEmail());
        msg.addRecipient(Message.RecipientType.TO, internetAddress);

        msg.setSubject(subject);
        msg.setContent(content, "text/html; charset=utf-8");
        msg.saveChanges();
        send(msg);

    }


    private InternetAddress getFromEmail() throws UnsupportedEncodingException {

        return new InternetAddress(from, domain);


    }


    public void sendAlert(final Entity entity,
                          final Point point,
                          final EmailAddress emailAddress,
                          final Value value, Subscription subscription) throws Exception {
        if (subscription.getNotifyMethod().equals(SubscriptionNotifyMethod.email)) {
            sendFormatedAlert(entity, point, emailAddress, value, subscription);
        }
        else {
            sendJson(entity, point, emailAddress, value, subscription);
        }

    }

    private void sendJson(final Entity entity,
                                   final Point point,
                                   final EmailAddress emailAddress,
                                   final Value value, Subscription subscription) throws Exception {

        ValueContainer valueContainer = new ValueContainer(
                entity.getOwner(), point.getId(), value
        );
        String json = GsonFactory.getInstance(true).toJson(valueContainer);

        sendEmail(emailAddress, json, subject, false);

    }
    private void sendFormatedAlert(final Entity entity,
                                   final Point point,
                                   final EmailAddress emailAddress,
                                   final Value value, Subscription subscription) throws Exception {
        final StringBuilder message = new StringBuilder(INT);

        message.append("<p>Data Point: ").append(entity.getName().getValue()).append("</p>");


        switch (value.getAlertState()) {
            case HighAlert: {
                message.append("<P>Alarm Status: High</P>")
                        .append("<P>Alarm Setting: ").append(point.getHighAlarm()).append("</P>")
                        .append("<p>Value Recorded: ").append(value.getDoubleValue()).append("</p>")
                        .append("<p>Data : ").append(value.getData()).append("</p>");
                break;
            }
            case LowAlert: {
                message.append("<P>Alarm Status: Low</P>")
                        .append("<P>Alarm Setting: ").append(point.getLowAlarm()).append("</P>")
                        .append("<p>Value : ").append(value.getDoubleValue()).append("</p>")
                        .append("<p>Data : ").append(value.getData()).append("</p>");
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
                        .append("<p>Data : ").append(value.getData()).append("</p>");

            }

        }


        message.append("<p></p>").append("<p><a href =\"").append(serverInfo.getFullServerURL(null)).append("?uuid=").append(point.getId()).append("\">Go to Current Status Report</a></p>");

        message.append("<P>Subscription Name: ").append(subscription.getName().getValue()).append(" </P>");
        message.append("<P>Subscription Description: ").append(subscription.getDescription()).append(" </P>");

        sendEmail(emailAddress, message.toString(), subject, true);

    }


    public void sendPasswordRecovery(String email, String token) throws Exception  {


        EmailAddress to = CommonFactory.createEmailAddress(email);


        String sb = "<P>Password Reset</p>" +
                "<a href = \"" + serverInfo.getFullServerURL(null) +
                "nimbits.html?" + Parameters.rToken.getText() + "=" + URLEncoder.encode(token, "UTF-8") + "\">Click here to reset your password</a>";


        String subject = "Nimbits Password Reset";
        sendEmail(to, sb, subject, true);


    }


}
