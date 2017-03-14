package com.nimbits.server.process.cron;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.ValueContainer;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class IncomingMailTask {

    private final static Logger logger = Logger.getLogger(IncomingMailTask.class.getName());

    @org.springframework.beans.factory.annotation.Value("${mail.incoming.host}")
    private String receivingHost;

    @org.springframework.beans.factory.annotation.Value("${mail.incoming.email}")
    private String email;

    @org.springframework.beans.factory.annotation.Value("${mail.incoming.password}")
    private String password;

    @org.springframework.beans.factory.annotation.Value("${mail.incoming.folder}")
    private String inbox;


    @org.springframework.beans.factory.annotation.Value("${mail.incoming.enabled}")
    private Boolean readMailEnabled;

    @org.springframework.beans.factory.annotation.Value("${mail.incoming.delete}")
    private Boolean deleteEnabled;

    @org.springframework.beans.factory.annotation.Value("${mail.incoming.protocol}")
    private String protocol;

    @org.springframework.beans.factory.annotation.Value("${mail.incoming.secret}")
    private String secret;



    private final EntityDao entityDao;


    private final ValueTask valueTask;

    private final UserDao userDao;

    @Autowired
    public IncomingMailTask(EntityDao entityDao, ValueTask valueTask, UserDao userDao) {
        this.entityDao = entityDao;
        this.valueTask = valueTask;
        this.userDao = userDao;
    }





    @Scheduled(
            fixedDelayString = "${mail.incoming.refreshRate}"
    )
    public void readMail() throws Exception {


        if (readMailEnabled) {
            Properties props = System.getProperties();

            props.setProperty("mail.store.protocol", protocol);

            Session session = Session.getDefaultInstance(props, null);

            Store store = session.getStore(protocol);

            store.connect(this.receivingHost, this.email, this.password);

            Folder folder = store.getFolder(inbox);//get inbox

            folder.open(Folder.READ_WRITE);//open folder only to read

            Message messages[] = folder.getMessages();

            for (Message message : messages) {

                logger.info(String.format("Processing incoming mail: %s", Arrays.toString(message.getFrom())));
                if (secret.equals(message.getSubject())) {
                    String body = getTextFromMessage(message);
                    Gson gson = GsonFactory.getInstance(true);
                    try {
                        ValueContainer valueContainer = gson.fromJson(body, ValueContainer.class);
                        Optional<User> user = userDao.getUserById(valueContainer.getOwner());
                        if (user.isPresent()) {
                            Optional<Entity> point = entityDao.getEntity(user.get(), valueContainer.getId(), EntityType.point);
                            if (point.isPresent()) {
                                logger.info("Incoming Email Data Successfully processed");
                                valueTask.process(user.get(), (Point) point.get(), valueContainer.getValue());
                            }

                        }
                    } catch (Throwable throwable) {
                        logger.log(Level.WARNING, "error processing incoming email", throwable);
                    }


                }
                else {
                    logger.warning("Message Marked as Spam and ignored");
                }


                message.setFlag(Flags.Flag.DELETED, deleteEnabled);

            }

            folder.close(true);

            store.close();


        }

    }


    private String getTextFromMessage(Message message) throws Exception {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws Exception{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                result = (String) bodyPart.getContent();

            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }
}
