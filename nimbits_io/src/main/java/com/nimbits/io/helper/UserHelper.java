package com.nimbits.io.helper;

import com.nimbits.client.model.user.User;

/**
 * Helper for working with the Session API REST Services on a Nimbits Server
 * Returns a User POJO if authentication was successful.
 *
 * @see com.nimbits.client.model.user.User
 */
@Deprecated //all helper classes are being replaced with the Nimbits.Builder() client
public interface UserHelper {

    /**
     * @return A user object or throws an exception if the user does not exist or access was denied.
     */
    User getSession();
}
