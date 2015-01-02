package com.nimbits.server.communication.email;


import com.nimbits.server.communication.mail.EmailService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})

public class EmailTemplateTest {

    @Autowired
    EmailService emailService;

    @Test
    public void testTemplateResources() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource("mail.html");
        File file = new File(url.getFile());
        TestCase.assertNotNull(file);


    }
}