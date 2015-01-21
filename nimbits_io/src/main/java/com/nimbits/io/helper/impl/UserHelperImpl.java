package com.nimbits.io.helper.impl;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.helper.UserHelper;
import com.nimbits.io.http.NimbitsClientFactory;


/**
 * Helper class to download a user's session data
 * @see com.nimbits.client.model.user.User
 *
 */
public class UserHelperImpl implements UserHelper {


    private final NimbitsClient client;

    public UserHelperImpl(Server server, EmailAddress emailAddress, String accessKey) {

        this.client = NimbitsClientFactory.getInstance(server, emailAddress, accessKey);
    }

    public UserHelperImpl(Server server, EmailAddress emailAddress) {

        this.client = NimbitsClientFactory.getInstance(server, emailAddress);
    }


    @Override
    public User getSession() {
        return client.getSession();


    }






}
