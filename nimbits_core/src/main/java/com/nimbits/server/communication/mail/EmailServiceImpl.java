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


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.auth.AuthService;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.system.ServerInfo;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.settings.SettingsService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {


    private SettingsService settingsService;


    private AuthService authService;


    private ServerInfo serverInfo;


    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class.getName());

    private static final int INT = 128;
    private static final int SECONDS_IN_MINUTE = 60;

    public EmailServiceImpl(SettingsService settingsService, AuthService authService, ServerInfo serverInfo) {
        this.settingsService = settingsService;
        this.authService = authService;
        this.serverInfo = serverInfo;
    }

    private void send(final Message msg) {
        String HOST = settingsService.getSetting(ServerSetting.smtp);
        String USER = settingsService.getSetting(ServerSetting.admin);
        String PASSWORD = settingsService.getSetting(ServerSetting.smtpPassword);

        try {
            Transport transport = authService.getMailTransport();

            if (transport != null) {
                transport.connect(HOST, USER, PASSWORD);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close();
            } else {
                Transport.send(msg); //send statically for GAE
            }

        } catch (MessagingException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }


    }


    private String createEmailFromTemplate(String message, String templateName) throws IOException {


        ClassLoader classLoader = getClass().getClassLoader();

        String html = IOUtils.toString(classLoader.getResourceAsStream(templateName));
        html = html.replace("MESSAGE_BODY_INSERTED", message);
        html = html.replace("INSTANCE_URL", serverInfo.getFullServerURL(null));
        html = html.replace("INSTANCE_NAME", serverInfo.getFullServerURL(null));
        return html;
    }


    @Override
    public void sendEmail(final EmailAddress emailAddress,
                          final String unformattedMessage,
                          final String subject) {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        try {

            final String content = createEmailFromTemplate(unformattedMessage, "mail.html");
            final InternetAddress internetAddress = new InternetAddress(emailAddress.getValue());
            final Message msg = new MimeMessage(session);
            msg.setFrom(getFromEmail());
            msg.addRecipient(Message.RecipientType.TO, internetAddress);

            msg.setSubject(subject);
            msg.setContent(content, "text/html; charset=utf-8");
            msg.saveChanges();
            send(msg);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private InternetAddress getFromEmail() throws UnsupportedEncodingException {
        final String fromEmail;
        fromEmail = settingsService.getSetting(ServerSetting.admin);
        return new InternetAddress(fromEmail, "nimbits.com");


    }


    @Override
    public void sendAlert(final Entity entity,
                          final Point point,
                          final EmailAddress emailAddress,
                          final Value value, Subscription subscription) {


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

        sendEmail(emailAddress, message.toString(), "Nimbits Subscription Event");

    }


    @Override
    public void sendConnectionRequest(User user, Connection c) {
        EmailAddress from = user.getEmail();
        EmailAddress to = CommonFactory.createEmailAddress(c.getTargetEmail());
        String url = serverInfo.getFullServerURL(null)
                + "/nimbits.html?" + Parameters.connectionId + "=" + c.getApprovalKey() + "&email=" + c.getTargetEmail();


        StringBuilder sb = new StringBuilder();
        sb
                .append("<P>" + user.getEmail().getValue() + " would like to connect nimbits accounts with you. </p>" +
                        "<P>Click here to approve (or copy and paste into your browser: </P>")

                .append("<a href=\"" + url + "\">" + url + "</a>")

                .append("<p> This will give ")
                .append(from.getValue())
                .append(" permission to read all of your nimbits data.  Also, you'll be able to read ")
                .append("all of their nimbits objects. Only do this if you know and trust the sender. </p>")
                .append("<p>Learn more about nimbits connections here:  <a href = \"http://www.nimbits.com/howto_connections.jsp\">Connections Tutorial</a></p>");
        ;


        String subject = "Nimbits Connection Request";
        sendEmail(to, sb.toString(), subject);

    }

    @Override
    public void sendConnectionRequestApprovalNotification(User user, Connection c) {


        EmailAddress to = CommonFactory.createEmailAddress(user.getName().getValue());
        String url = serverInfo.getFullServerURL(null)
                + "/nimbits.html";


        StringBuilder sb = new StringBuilder();
        sb
                .append("<P>" + c.getTargetEmail() + " has approved your connection request. You can now login to the originating nimbits sever to view their data. </P>")

                .append("<a href=\"http://" + url + "\">" + url + "</a>")


                .append("<p>Learn more about nimbits connections here:  <a href = \"http://www.nimbits.com/howto_connections.jsp\">Connections Tutorial</a></p>");


        String subject = "Nimbits Connection Request";
        sendEmail(to, sb.toString(), subject);

    }

    @Override
    public void sendPasswordRecovery(String email, String token) {


        EmailAddress to = CommonFactory.createEmailAddress(email);


        StringBuilder sb = new StringBuilder();
        sb.append("<P>Password Reset</p>");
        sb.append("<a href = \"" + serverInfo.getFullServerURL(null));
        try {
            sb.append("nimbits.html?" + Parameters.rToken.getText() + "=" + URLEncoder.encode(token, "UTF-8") + "\">Click here to reset your password</a>");


            String subject = "Nimbits Password Reset";
            sendEmail(to, sb.toString(), subject);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


}
