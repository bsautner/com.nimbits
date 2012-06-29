package com.nimbits.server.service.email.impl;

import com.nimbits.server.service.email.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * User: benjamin
 * Date: 6/28/12
 * Time: 12:01 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@Transactional
@Service("emailService")
public class EmailServiceImpl implements EmailService {
    @Resource(name="mailSender")
    private MailSender mailSender;

    @Resource(name="templateMessage")
    private SimpleMailMessage templateMessage;


    @Override
    public void sendTest() {


        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo("bsautner@gmail.com");
        msg.setText(
                "Dear");
        try{
            this.mailSender.send(msg);
        }
        catch(MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }

    }

    @Override
    public void send(String message) {
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo("support@nimbits.com");
        msg.setText(message);
        try{
            this.mailSender.send(msg);
        }
        catch(MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }


}
