package com.nimbits.io;

import com.nimbits.client.io.NimbitsClient;
import com.nimbits.client.io.HttpClientFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;


/**
 * Helper class to download a user's session data
 * @see com.nimbits.client.model.user.User
 *
 */
public class UserHelper {


    private final NimbitsClient client;

    public UserHelper(Server server, EmailAddress emailAddress) {

        this.client = HttpClientFactory.getInstance(server, emailAddress);
    }

    /**
     *
     * @param params should include an access key or an apiKey
     * @return A user object or throws an exception if the user does not exist or access was denied.
     */
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
