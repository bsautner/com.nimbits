/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
 */

import com.google.common.collect.Range;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.io.helper.*;

import java.util.*;

/**
 * This program was designed to hammer the holy heck out of a nimbits instance to see how it performs.
 *
 * This will pump in a years worth of data and then verify it's been stored.
 *
 *
 *
 */
public class SystemHealthCheck2 {
    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("support@nimbits.com");

    //this sample uses an access key, so you've logged into nimbits and right clicked on your account to create this read/write key with user scope.

    private static final String ACCESS_KEY = "key";

    //set this to your appid.appspot.com if on google app engine, cloud.nimbits.com for the public cloud,
    //and localhost:8080 if that's your jetty local instance for example.
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL);



    public static void main(String[] args) throws InterruptedException {

    }



}
