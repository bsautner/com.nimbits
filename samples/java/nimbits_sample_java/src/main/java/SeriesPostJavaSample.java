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
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.AccessToken;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import com.nimbits.io.helper.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

/**
 * This program will create 10 data points, add 1000 values to each point, and post all 10,000 values to
 * a local nimbits server using the Batch API.  It will repeat the process 100 times, and then verify the
 * 1 Million Values are available for download. We run this against new builds to ensure data integrity Buckle Up.
 *
 * How to Use:
 *
 * 1. Login to your nimbits server, make sure the email address used below matches the server admin
 * 2. Right click on your user and select to create a read/write key - here the key is just the word "key"
 * 3. Verify the url below with the port number etc.
 *
 */
public class SeriesPostJavaSample {
    //private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("pi@localhost.com");
//    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("test@example.com");
//    private static final AccessToken TOKEN = AccessToken.getInstance("key");
//     private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost:8085");

    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("b@b.com");
    private static final AccessToken TOKEN = AccessToken.getInstance("b");
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost:8888");
    // private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost:8080");
    // private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("cloud.nimbits.com");
    private static final Server SESSION_START = ServerFactory.getInstance(INSTANCE_URL, EMAIL_ADDRESS, TOKEN);

    protected static final int COUNT = 10;
    public static final int VCOUNT = 10;
    public static final int ROUNDS = 10000;

    static ValueHelper valueHelper;
    static PointHelper pointHelper;
    static EntityHelper entityHelper;
    static User user;
    static int calcErrors = 0;

    public static void main(String[] args) throws InterruptedException {


        System.out.println("Welcome To Nimbits!");

        //some random name - can be anything but duplicates are not allowed.


        UserHelper sessionHelper = HelperFactory.getUserHelper(SESSION_START);

        user = sessionHelper.getSession();
        AccessToken sessionToken = AccessToken.getInstance(user.getToken());
        Server loggedInServer = ServerFactory.getInstance(INSTANCE_URL, EMAIL_ADDRESS, sessionToken);
        valueHelper = HelperFactory.getValueHelper(loggedInServer);
        pointHelper = HelperFactory.getPointHelper(loggedInServer);
        entityHelper = HelperFactory.getEntityHelper(loggedInServer);
        System.out.println("Hello " + user.getEmail() + " " + user.getToken());

        long size = FileUtils.sizeOfDirectory(new File("/opt/nimbits/data/" + user.getEmail()));
        deleteEverything();
        //   go();
        testStats();
        deleteEverything();
        long size2 = FileUtils.sizeOfDirectory(new File("/opt/nimbits/data/" + user.getEmail()));
        List<Entity> stree = entityHelper.getTree();
        System.out.println("Start dir size: " + size + " end " + size2 + " junk: " + (size2 - size));
        System.out.println("Entities Left: " + stree.size());

    }

    private static void deleteEverything() {
        List<Entity> stree = entityHelper.getTree();
        System.out.println("Items in Tree" + stree.size());
        for (Entity e : stree) {

            if (!e.getEntityType().equals(EntityType.user)) {
                System.out.println("Deleting " + e.getName().getValue());
                if (e.getParent().equals(user.getEmail().getValue())) {
                    entityHelper.deleteEntity(e);
                }
            }
        }
    }

    private static void go() {

        List<Point> points = new ArrayList<>(COUNT);

        for (int i = 0; i < COUNT; i++) {
            String pointName = UUID.randomUUID().toString();
            Point point = pointHelper.createPoint(pointName, "Some Random Description");
            points.add(point);
            System.out.println("Created Point " + point.getKey());

        }


        //  boolean success = pointHelper.pointExists(pointName);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        calendar.add(Calendar.SECOND, 1);
        Date start = calendar.getTime();
        System.out.println(calendar.getTime());
        Random r = new Random();


        for (int repeat = 0; repeat < ROUNDS; repeat++) {
            System.out.println("ROUND: " + repeat + " CALC ERRORS: " + calcErrors);

            for (Point point : points) {
                point.getValues().clear();

                List<Value> values = new ArrayList<>();
                for (int i = 0; i < VCOUNT; i++) {

                    Value value = new Value.ValueBuilder().doubleValue(r.nextDouble() * 100).timestamp(calendar.getTime()).createValue();
                    values.add(value);
                    point.getValues().add(value);
                    calendar.add(Calendar.SECOND, 1);
                }

            }

            valueHelper.recordValues(points);



            for (Point point : points) {
                List<Value> recordedValues = valueHelper.getSeries(point.getName().getValue());
                System.out.println("downloaded " + recordedValues.size() + " Stored Values.");
                if (recordedValues.size() !=  VCOUNT - 1) {

                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            testCalculations( );
            testStats();



            for (Point point : points) {
                List<Value> recordedValues = valueHelper.getSeries(point.getName().getValue());
                System.out.println("downloaded " + recordedValues.size() + " Stored Values.");
            }
        }

        Date end = calendar.getTime();
        for (Point point : points) {
            List<Value> recordedValues = valueHelper.getSeries(point.getName().getValue(),
                    Range.closed(start, end));
            System.out.println("Downloads All Values:" + recordedValues.size());

        }



        List<Entity> tree2 = entityHelper.getTree();


        System.out.println("Items left in Tree After Purge " + tree2.size());

        for (Entity e : tree2) {
            System.out.println("Remaining:: " + e.getParent() + "/" + e.getName().getValue() + " " + e.getEntityType().name());

        }
        System.out.println("Done!");

    }


    /**
     Creates two data points and a summary trigger to compute an average etc
     **/
    private static void testStats() {
        List<Point> cvs = new ArrayList<>(3);

        String targetPointName = "STAT_TARGET" + UUID.randomUUID().toString();
        Point targetPoint = pointHelper.createPoint(targetPointName, "Some Random Description");

        String triggerPointName = "STAT_TRIGGER" + UUID.randomUUID().toString();
        Point triggerPoint = pointHelper.createPoint(triggerPointName, "Some Random Description");



        Summary summary = entityHelper.createSummary(UUID.randomUUID().toString(), triggerPoint.getKey(), targetPoint.getKey(), SummaryType.average, 1000);

        Random r = new Random();

        System.out.println("created summary" + summary.getKey());
        Double c = 0.0;
        long time = System.currentTimeMillis() + 60000;
        int count = 0;
        while (System.currentTimeMillis() < time) {

            double d = r.nextDouble() * 100;

            valueHelper.recordValue(triggerPointName, d);
            System.out.println("Recording " + count++ + " " + d);
        }
        List<Value> values = valueHelper.getSeries(targetPointName);
        for (Value value : values) {
            System.out.println("Result " + value.getDoubleValue());
        }

        System.out.println("Stat Result should be " + c);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Value value = valueHelper.getValue(targetPoint.getName().getValue());
        System.out.println("avg for target point = " + value.getDoubleValue());


        entityHelper.deleteEntity(targetPoint);
        entityHelper.deleteEntity(triggerPoint);



    }

    private static void testCalculations() {
        List<Point> cvs = new ArrayList<>(3);
        Random r = new Random();
        String[] xyz = {"x", "y", "z"};
        for (int i = 0; i < 3; i++) {
            String pointName = xyz[i] + "CV" + UUID.randomUUID().toString();
            Point point = pointHelper.createPoint(pointName, "Some Random Description");
            cvs.add(point);
            System.out.println("Created Point " + point.getKey());


        }

        String targetPointName = "TARGET" + UUID.randomUUID().toString();
        Point targetPoint = pointHelper.createPoint(targetPointName, "Some Random Description");


        Calculation calculation = entityHelper.createCalculation("CALC" + UUID.randomUUID().toString(), cvs.get(2).getKey(),
                targetPoint.getKey(), "x+y+z", cvs.get(0).getKey(), cvs.get(1).getKey(), cvs.get(2).getKey());


        System.out.println("created calc" + calculation.getKey());
        Double c = 0.0;
        for (int i = 0; i < 3; i++) {
            double d = r.nextDouble() * 100;
            c += d;
            valueHelper.recordValue(cvs.get(i).getName().getValue(), d);
            System.out.println("Recording " + d  + " cuml: " + c);

        }
        System.out.println("Calc Result should be " + c);


        Value value = valueHelper.getValue(targetPoint.getName().getValue());

        Value v1 = valueHelper.getValue(cvs.get(0).getName().getValue());

        Value v2 = valueHelper.getValue(cvs.get(1).getName().getValue());

        Value v3 = valueHelper.getValue(cvs.get(2).getName().getValue());

        System.out.println("Calculated Value = " + value.getDoubleValue());
        System.out.println("Calculated Value Should be = " + (v1.getDoubleValue() + v2.getDoubleValue() + v3.getDoubleValue()));
        if (Double.compare(value.getDoubleValue(), c) != 0) {
            calcErrors++;
        }
        for (Point point : cvs) {
            entityHelper.deleteEntity(point);
        }
        entityHelper.deleteEntity(targetPoint);



    }

}

