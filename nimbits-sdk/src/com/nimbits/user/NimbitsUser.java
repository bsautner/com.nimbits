package com.nimbits.user;

import com.nimbits.client.model.email.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/13/11
 * Time: 12:31 PM
 */
public class NimbitsUser {
    private EmailAddress emailAddress;
    private String nimbitsSecretKey;

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public String getNimbitsSecretKey() {
        return this.nimbitsSecretKey;
    }


    private NimbitsUser() {
    }

    public NimbitsUser(final EmailAddress emailAddress, final String k) {
        this.emailAddress = emailAddress;
        this.nimbitsSecretKey = k;
    }

}
