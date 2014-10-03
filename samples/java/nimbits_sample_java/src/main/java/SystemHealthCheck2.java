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
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.UserHelper;
import com.nimbits.io.helper.ValueHelper;

import java.text.SimpleDateFormat;
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
    //and localhost:8080/nimbits if that's your jetty local instance for example.
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost:8080/nimbits");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL);

    private static ValueHelper valueHelper;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome To Nimbits!");

        //use an access key you created via the web console to get your user data
        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        EntityHelper helper = HelperFactory.getEntityHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        User user = sessionHelper.getSession();
        System.out.println("Hello " + user.getEmail());
        String name = "BEN" + UUID.randomUUID().toString();
        helper.createPoint(name, 1000, EntityType.point, user);
        //record some data!

        Random r = new Random();
        valueHelper = HelperFactory.getValueHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        Calendar start = Calendar.getInstance();
        start.setTime(c.getTime());
        long endTest = System.currentTimeMillis();

        int count = 0;
        while (c.getTimeInMillis() < endTest) {
            List<Value> data = new ArrayList<>(1000);

            c.add(Calendar.MINUTE, 60);
            Value value = ValueFactory.createValueModel(r.nextDouble() * 10000, c.getTime());

            count++;

            System.out.println("recording " +  c.getTime());
            valueHelper.recordValue(name, value);

        }



        System.out.println("logged" + count);
        valueHelper.moveCron();
        Thread.sleep(5000);
        List<Value> download = new ArrayList<>(count);
        List<Value> series1 = valueHelper.getSeries(name);
        System.out.println("got back (should be " + count + ")" + series1.size());
        while (start.getTimeInMillis() < endTest) {

            Date s = new Date(start.getTimeInMillis());
            start.add(Calendar.MINUTE, 60);
            Date e = new Date(start.getTimeInMillis());







            Range<Date> range = Range.closed(s, e);

            List<Value> series = valueHelper.getSeries(name, range);
            System.out.println(s + ">>" + e + " " + series.size());
            download.addAll(series);
        }
        System.out.println("got back " + download.size());


//        List<Value> series = valueHelper.getSeries(name);
//        System.out.println("got back " + series.size());

        helper.deleteEntity(name, EntityType.point);
        System.out.println("deleted " + name);

        helper.createPoint(name, 1000, EntityType.point, user);
        List<Value> series2 = valueHelper.getSeries(name);
        System.out.println("got back (should be zero)" + series2.size());

    }



}
