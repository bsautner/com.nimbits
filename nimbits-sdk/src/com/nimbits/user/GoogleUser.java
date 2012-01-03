package com.nimbits.user;

import com.nimbits.client.model.email.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/13/11
 * Time: 12:28 PM
 */
public class GoogleUser {
    private EmailAddress googleEmailAddress;
    private String googlePassword;

    public EmailAddress getGoogleEmailAddress() {
        return googleEmailAddress;
    }

    public String getGooglePassword() {
        return this.googlePassword;
    }

    private GoogleUser() {
    }

    public GoogleUser(final EmailAddress googleEmailAddress, final String googlePassword) {
        this.googleEmailAddress = googleEmailAddress;
        this.googlePassword = googlePassword;
    }


}
