package com.nimbits.io;

import com.nimbits.client.model.user.User;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public interface UserHelper extends EntityHelper {

    /**
     *
     * @param params should include an access key or an apiKey
     * @return A user object or throws an exception if the user does not exist or access was denied.
     */
    User getSession(List<BasicNameValuePair> params);
}
