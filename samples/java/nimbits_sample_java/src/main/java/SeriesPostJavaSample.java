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

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.PointHelper;
import com.nimbits.io.helper.UserHelper;
import com.nimbits.io.helper.ValueHelper;

import java.util.*;

/**
 * This program will create 10 data points, add 1000 values to each point, and post all 10,000 values to
 * a local nimbits server using the Batch API.  It will repeat the process 100 times, and then verify the
 * 1 Million Values are available for download.  Buckle Up.
 *
 * How to Use:
 *
 * 1. Login to your nimbits server, make sure the email address used below matches the server admin
 * 2. Right click on your user and select to create a read/write key - here the key is just the word "key"
 * 3. Verify the url below with the port number etc.
 *
 */
public class SeriesPostJavaSample {
    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("support@nimbits.com");
    private static final String ACCESS_KEY = "key";
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost:8081");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL);
    protected static final int COUNT = 10;


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome To Nimbits!");

        //some random name - can be anything but duplicates are not allowed.

        ValueHelper valueHelper = HelperFactory.getValueHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        PointHelper pointHelper = HelperFactory.getPointHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        User user = sessionHelper.getSession();


        System.out.println("Hello " + user.getEmail());


        List<Point> points = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            String pointName = UUID.randomUUID().toString();
            Point point = pointHelper.createPoint(pointName, "Some Random Description");
            points.add(point);
            System.out.println("Created Point " + pointName);

        }

        //  boolean success = pointHelper.pointExists(pointName);
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.SECOND, 10000);
        System.out.println(calendar.getTime());



        for (Point point : points) {



            List<Value> values = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {

                Value value = ValueFactory.createValueModel((double) calendar.getTimeInMillis(), calendar.getTime());
                values.add(value);
                point.getValues().add(value);
                calendar.roll(Calendar.SECOND, 1);
            }
        }

        valueHelper.recordValues(points);

        for (Point point : points) {
            List<Value> recordedValues = valueHelper.getSeries(point.getName().getValue());
            System.out.println("downloaded " + recordedValues.size() + " Cached Values.");
        }

        Map<String, Integer> moveMap = valueHelper.moveCron();
        System.out.println("moveMap contained (should be " + COUNT + ") " + moveMap.size());
        for (String name : moveMap.keySet()) {
            System.out.println(name + " moved " + moveMap.get(name));
        }

        Map<String, Integer> verify = valueHelper.moveCron();
        System.out.println("verify contained (should be zero)" + verify.size());
        for (String name : verify.keySet()) {
            System.out.println(name + " moved " + verify.get(name));
        }

        for (Point point : points) {
            List<Value> recordedValues = valueHelper.getSeries(point.getName().getValue());
            System.out.println("downloaded " + recordedValues.size() + " Stored Values.");
        }

    }

}

