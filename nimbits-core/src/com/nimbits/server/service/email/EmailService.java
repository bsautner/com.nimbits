package com.nimbits.server.service.email;

/**
 * User: benjamin
 * Date: 6/28/12
 * Time: 12:12 PM
 * Copyright 2012 Nimbits Inc - All Rights Reserved
 */
public interface EmailService {
    void sendTest();

    void send(String message);
}
