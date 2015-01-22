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
     * @param server A sever object containing the instance url, email and password, key or api key

      * @return UserHelper
     */
    public static UserHelper getUserHelper(Server server) {

        return new UserHelperImpl(server);

    }

    /**
     * For creating, deleting and updating data points
     * @param server A sever object containing the instance url, email and password, key or api key
     * @return PointHelper
     */
    public static PointHelper getPointHelper(Server server) {

        return new PointHelperImpl(server);

    }


    /**
     * For recording and downloading time series data values without an API KEY
     * @param server A sever object containing the instance url, email and password, key or api key
     * @return ValueHelper
     */
    public static ValueHelper getValueHelper(Server server) {

        return new ValueHelperImpl(server);

    }

    /**
     * For creating, deleting and updating entities
     * @param server A sever object containing the instance url, email and password, key or api key
     * @return EntityHelper
     */
    public static EntityHelper getEntityHelper(Server server) {

        return new EntityHelperImpl(server);

    }

}
