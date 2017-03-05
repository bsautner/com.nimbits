package com.nimbits.it;

import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Exercises the V3 Rest API - designed to run continuously against a server as an integration test.
 */
public class V3RestClientTester extends NimbitsTest {

    private List<Entity> pointList = new ArrayList<>();
    private Map<Entity, List<Value>> storedValues = new HashMap<Entity, List<Value>>();

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    @Ignore
    public void executeTest() throws InterruptedException {

        log("Starting up");

        if (user != null) {
            log("Continuing with user: " + user.getEmail() + " " + user.getId());

            createRegularUsers();
            createPoints();
            Thread.sleep(5000);
            recordSeriesData();
        } else {
            log("User was null! Exiting Test");
        }

        log("Done!");
    }

    private void createRegularUsers() {

        for (int i = 0; i < 10; i++) {
            log("Creating regular user " + i);
            String password = "password";
            User regularUser = createUser(UUID.randomUUID().toString() + "@example.com", password);

            Nimbits nonAdminClient = new Nimbits.Builder()
                    .email(regularUser.getEmail().getValue())
                    .token(password)
                    .instance(INSTANCE_URL).create();

            User verify = nonAdminClient.getMe(true);

            if (verify.equals(regularUser)) {
                log("Verified Creating Regular " + i + " User can login ");
            } else {
                throw new RuntimeException("Could not verify regular user");
            }


        }
    }

    /**
     * Create some data points with the new user as the parent
     */
    private void createPoints() {

        for (int i = 0; i < 10; i++) {
            Point point = new PointModel.Builder().name(UUID.randomUUID().toString()).parent(user.getId())
                    .create();
            Entity newPoint = nimbits.addPoint(user, point);
            pointList.add(newPoint);
            log("Created : " + newPoint.getName().getValue());
        }

    }

    /**
     * Record large amounts of data to each point
     */

    private void recordSeriesData() throws InterruptedException {
        log("Recording Data");
        Random r = new Random();
        String[] meta = {"foo", "bar"};

        for (Entity entity : pointList) {

            List<Value> values = new ArrayList<Value>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -30);
            for (int i = 0; i < 1000; i++) {

                calendar.add(Calendar.SECOND, 1);
                String metavalue = meta[(i & 1) == 0 ? 0 : 1]; //flip between meta values for testing search
                values.add(new Value.Builder()
                        .timestamp(calendar.getTimeInMillis())
                        .doubleValue(r.nextDouble() * 1000)
                        .lat(0.0)
                        .lng(0.0)
                        .meta(metavalue).data("{}").create());

            }
            nimbits.recordValues(entity, values);
            log("Recorded : " + values.size() + " for " + entity.getName());
            storedValues.put(entity, values);

        }

        log("Waiting for things to settle down server side");
        Thread.sleep(60000);
        log("Verifying Data");
        verifySeriesData(null);

        Thread.sleep(1000);
        log("Verifying Data again!");
        verifySeriesData("");

        log("Verifying Data again with perfect match mask!");
        verifySeriesData(meta[0]);

        log("Verifying Data again with other perfect match mask mask!");
        verifySeriesData(meta[1]);

        log("Verifying Data again with regex mask!");
        verifySeriesData("[o]+");

        log("Make sure Count param is working");
        verifyCountParam();


    }

    private void verifySeriesData(String mask) throws InterruptedException {
        for (Entity entity : pointList) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -31);
            log(entity.getId());
            List<Value> downloadedValues = nimbits.getValues(entity, calendar.getTime(), new Date(), mask);
            log("Downloaded " + downloadedValues.size() + " for " + entity.getName());


            Thread.sleep(5000);
            List<Value> stored = storedValues.get(entity);
            Collections.sort(stored);
            Collections.sort(downloadedValues);


            for (Value value : stored) {
                if (StringUtils.isEmpty(mask) || mask.equals(value.getMetaData()) || containsMask(value, mask))
                    if (!downloadedValues.contains(value)) {
                        log("R Range: " + stored.get(0).getLTimestamp() + " to " +
                                stored.get(stored.size() - 1).getLTimestamp());
                        log("Q Range: " + calendar.getTime() + " to " + new Date());


                        error(
                                "downloaded values did not contain expected posted value. " + value.toString());


                    }
            }

        }
    }

    private void verifyCountParam() {
        for (Entity entity : pointList) {
            for (int i = 1; i < 1000; i++) {
                log("getting " + i + " values for " + entity.getName());
                List<Value> values = nimbits.getValues(entity, i);
                if (values.size() != i) {
                    error("asked for " + i + " values but got " + values.size());
                }

            }


        }


    }


    public static boolean containsMask(Value value, String mask) {

        if (StringUtils.isEmpty(mask)) {
            return true;
        } else if (!StringUtils.isEmpty(mask) && mask.equals(value.getMetaData())) {

            return true;
        } else if (!StringUtils.isEmpty(mask)) {
            try {
                Pattern p = Pattern.compile(mask);
                Matcher m = p.matcher(value.getMetaData());
                return m.find();
            } catch (PatternSyntaxException ex) {

                return false;
            }


        }

        return false;

    }


}
