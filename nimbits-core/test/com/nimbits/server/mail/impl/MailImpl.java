package com.nimbits.server.mail.impl;

import com.nimbits.server.service.email.EmailService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


/**
 * User: benjamin
 * Date: 6/28/12
 * Time: 11:50 AM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */ @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations={
            "classpath:META-INF/applicationContext.xml",
            "classpath:META-INF/applicationContext-service.xml",
            "classpath:META-INF/applicationContext-daos.xml"
    })
public class MailImpl {

    SimpleMailMessage templateMessage;
    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    @Resource(name="emailService")
    private EmailService service;

    @Test
    @Ignore
    public void sendTest() {

        service.sendTest();



    }

}
