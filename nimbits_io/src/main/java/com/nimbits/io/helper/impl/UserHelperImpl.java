package com.nimbits.io.helper.impl;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.helper.UserHelper;
import com.nimbits.io.http.NimbitsClientFactory;

import java.util.List;


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
        List<User> result = client.getSession();
        if (result.isEmpty()) {
            throw new IllegalArgumentException("User not found or access denied.");
        }
        else {
            return result.get(0);
        }

    }






}
