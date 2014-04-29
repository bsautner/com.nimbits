package com.nimbits.helper;

import com.nimbits.client.io.HttpClient;
import com.nimbits.client.io.HttpClientFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public class UserHelper {


    private final HttpClient client;

    public UserHelper(Server server, EmailAddress emailAddress) {

        this.client = HttpClientFactory.getInstance(server, emailAddress);
    }

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
