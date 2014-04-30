package com.nimbits.io;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.impl.PointHelperImpl;
import com.nimbits.io.impl.UserHelperImpl;
import com.nimbits.io.impl.ValueHelperImpl;

public class HelperFactory {

    public static UserHelper getUserHelper(Server server, EmailAddress emailAddress, String accessKey) {

        return new UserHelperImpl(server, emailAddress, accessKey);

    }

    public static PointHelper getPointHelper(Server server, EmailAddress emailAddress, String accessKey) {

        return new PointHelperImpl(server, emailAddress, accessKey);

    }

    public static ValueHelper getValueHelper(Server server, EmailAddress emailAddress, String accessKey) {

        return new ValueHelperImpl(server, emailAddress, accessKey);

    }
}
