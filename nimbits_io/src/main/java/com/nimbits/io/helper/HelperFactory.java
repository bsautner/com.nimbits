package com.nimbits.io.helper;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.helper.impl.EntityHelperImpl;
import com.nimbits.io.helper.impl.PointHelperImpl;
import com.nimbits.io.helper.impl.UserHelperImpl;
import com.nimbits.io.helper.impl.ValueHelperImpl;

/**
 * Convenience class to get helpers for recorded data, getting sessions and points etc.
 */
public class HelperFactory {

    /**
     * For getting the user session data. Helpful for testing credentials.
     * @param server A sever object containing the instance url, Optional API KEY etc
     * @param emailAddress The account owner
     * @param accessKey nullable access key, optional if you want to provide an access key
     * @return UserHelper
     */
    public static UserHelper getUserHelper(Server server, EmailAddress emailAddress, String accessKey) {

        return new UserHelperImpl(server, emailAddress, accessKey);

    }

    /**
     * For getting the user session data. Helpful for testing credentials.
     * @param server A sever object containing the instance url, MUST CONSTAIN AN API KEY etc
     * @param emailAddress The account owner
      * @return UserHelper
     */
    public static UserHelper getUserHelper(Server server, EmailAddress emailAddress) {

        return new UserHelperImpl(server, emailAddress);

    }

    /**
     * For creating, deleting and updating data points
     * @param server A sever object containing the instance url, Optional API KEY etc
     * @param emailAddress The account owner
     * @param accessKey nullable access key, optional if you want to provide an access key
     * @return PointHelper
     */
    public static PointHelper getPointHelper(Server server, EmailAddress emailAddress, String accessKey) {

        return new PointHelperImpl(server, emailAddress, accessKey);

    }

    /**
     * For recording and downloading time series data values
     * @param server A sever object containing the instance url, Optional API KEY etc
     * @param emailAddress The account owner
     * @param accessKey nullable access key, optional if you want to provide an access key
     * @return ValueHelper
     */
    public static ValueHelper getValueHelper(Server server, EmailAddress emailAddress, String accessKey) {

        return new ValueHelperImpl(server, emailAddress, accessKey);

    }

    /**
     * For recording and downloading time series data values without an API KEY
     * @param server A sever object containing the instance url, REQUIRES AN API KEY etc
     * @param emailAddress The account owner

     * @return ValueHelper
     */
    public static ValueHelper getValueHelper(Server server, EmailAddress emailAddress ) {

        return new ValueHelperImpl(server, emailAddress);

    }

    /**
     * For creating, deleting and updating entities
     * @param server A sever object containing the instance url, Optional API KEY etc
     * @param emailAddress The account owner
     * @param accessKey nullable access key, optional if you want to provide an access key
     * @return EntityHelper
     */
    public static EntityHelper getEntityHelper(Server server, EmailAddress emailAddress, String accessKey) {

        return new EntityHelperImpl(server, emailAddress, accessKey);

    }
    /**
     * For creating, deleting and updating entities
     * @param server A sever object containing the instance url, MUST BE CONSTRUCTED WITH AN API KEY etc
     * @param emailAddress The account owner
     * @return EntityHelper
     */
    public static EntityHelper getEntityHelper(Server server, EmailAddress emailAddress) {
        return new EntityHelperImpl(server, emailAddress, null);
    }

    public static PointHelper getPointHelper(Server server, EmailAddress emailAddress) {
        return new PointHelperImpl(server, emailAddress, null);
    }
}
