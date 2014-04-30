package com.nimbits.io.impl;

import com.nimbits.client.io.NimbitsClient;
import com.nimbits.client.io.HttpClientFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import com.nimbits.io.UserHelper;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;


/**
 * Helper class to download a user's session data
 * @see com.nimbits.client.model.user.User
 *
 */
public class UserHelperImpl implements UserHelper {


    private final NimbitsClient client;

    public UserHelperImpl(Server server, EmailAddress emailAddress, String accessKey) {

        this.client = HttpClientFactory.getInstance(server, emailAddress);
    }


    @Override
    public User getSession(List<BasicNameValuePair> params) {
        List<User> result = client.getSession(params);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("User not found or access denied.");
        }
        else {
            return result.get(0);
        }

    }






}
