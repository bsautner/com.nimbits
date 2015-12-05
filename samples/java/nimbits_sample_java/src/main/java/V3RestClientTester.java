import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.Nimbits;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Exercises the V3 Rest API - designed to run continuously against a server as an integration test.
 *
 *
 */
public class V3RestClientTester {
    private static final String EMAIL_ADDRESS ="admin@example.com";
    private static final String INSTANCE_URL = "http://localhost:8080";
    private static final String PASSWORD = "password1234";
    private static final Nimbits nimbits = new Nimbits.Builder()
            .email(EMAIL_ADDRESS).token(PASSWORD).instance(INSTANCE_URL).create();

    public static void main(String[] args) throws InterruptedException {


        o("Starting up");




        NimbitsLoadTester loadTester = new NimbitsLoadTester();
        loadTester.execute();


    }

    private static class NimbitsLoadTester {
        private User user;
        private List<Entity> pointList = new ArrayList<>();
        private Map<Entity, List<Value>> storedValues = new HashMap<>();

        public void execute() throws InterruptedException {

            //Get or Create this user account, when created it will be the system admin
            user = verifyAdminUser();

            //reset the users password to test that
            user.setPassword(UUID.randomUUID().toString());


            if (user != null) {
                o("Continuing with user: " + user.getEmail() + " " + user.getUUID());

                createRegularUsers();
                createPoints();
                recordSeriesData();
            }
            else {
                o("User was null! Exiting Test");
            }

            o("Done!");
        }

        private void createRegularUsers() {

            for (int i = 0; i < 10; i++) {
                o("Creating regular user " + i);
                String password = UUID.randomUUID().toString();
                User regularUser = createUser(UUID.randomUUID().toString() + "@example.com", password);

                Nimbits nonAdminClient = new Nimbits.NimbitsBuilder()
                        .email(regularUser.getEmail().getValue()).token(password).instance(INSTANCE_URL).create();
                User verify = nonAdminClient.getMe();
                if (verify.equals(regularUser)) {
                    o("Verified Creating Regular " + i + " User can login ");
                }
                else {
                    throw new RuntimeException("Could not verify regular user");
                }


            }
        }


        /**
         * Try to get my user from the root of the service/v2/rest/me uri - or create it by posting new user credentials to
         * /service/v2/rest
         */

        private User verifyAdminUser() {
            //See if my user id and password get me my user
            User user;

            try {

                o("Trying to get existing user info");
                user = nimbits.getMe();
            } catch (Throwable throwable) {
                //user not found, let's create on - the first user will be an admin of the server
                o("Server returned error - creating user instead " + throwable.getMessage());
                user =  createUser(EMAIL_ADDRESS, PASSWORD);

            }

            o("Got User:" + user.toString());

            User verify = nimbits.getMe();
            return verify;



        }

        /**
         * Creates a new user, if this is the first user on the system, it will be the admin
         *
         *
         */
        private User createUser(String email, String password) {

            User postObject = new UserModel(email, password);
            User newUser = nimbits.addUser(postObject);
            if (newUser == null || ! newUser.getEmail().getValue().equals(email)) {
                throw new RuntimeException("Could not create a new user");
            }
            else {
                o("Create new user" + newUser.getEmail());
                return newUser;
            }
        }

        /**
         * Create some data points with the new user as the parent
         */
        private void createPoints() {

            for (int i = 0; i < 10; i++) {
                Point point = new PointModel(user, UUID.randomUUID().toString() + " point name " + i);
                Entity newPoint =  nimbits.addEntity(user, point);
                pointList.add(newPoint);
                o("Created : " + newPoint.getName().getValue());
            }

        }

        /**
         * Record large amounts of data to each point
         */

        private void recordSeriesData() throws InterruptedException {
            o("Recording Data");
            Random r = new Random();
            String[] meta = {"foo", "bar"};

            for (Entity entity : pointList) {

                List<Value> values = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                for (int i = 0; i < 1000; i++) {

                    calendar.add(Calendar.SECOND, 1);
                    String metavalue = meta[(i & 1) == 0 ? 0 : 1]; //flip between meta values for testing search
                    values.add(new Value.Builder().timestamp(calendar.getTime()).doubleValue(r.nextDouble() * 1000).meta(metavalue).data("{}").create());

                }
                nimbits.recordValues(entity, values);
                o("Recorded : " + values.size() + " for " + entity.getName());
                storedValues.put(entity, values);

            }

            o("Waiting for things to settle down server side");
            Thread.sleep(5000);
            o("Verifying Data");
            verifySeriesData(null);

            Thread.sleep(1000);
            o("Verifying Data again!");
            verifySeriesData("");

            o("Verifying Data again with perfect match mask!");
            verifySeriesData(meta[0]);

            o("Verifying Data again with other perfect match mask mask!");
            verifySeriesData(meta[1]);

            o("Verifying Data again with regex mask!");
            verifySeriesData("[o]+");

            o("Make sure Count param is working");
            verifyCountParam();



        }

        private void verifySeriesData(String mask) {
            for (Entity entity : pointList) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -31);
                o(entity.getUUID());
                List<Value> downloadedValues = nimbits.getValues(entity, calendar.getTime(), new Date(), mask);
                o("Downloaded " + downloadedValues.size() + " for " + entity.getName());
                List<Value> stored = storedValues.get(entity);
                Collections.sort(stored);
                Collections.sort(downloadedValues);


                    for (Value value : stored) {
                        if (StringUtils.isEmpty(mask) || mask.equals(value.getMetaData()) || containsMask(value, mask))
                        if (!downloadedValues.contains(value)) {
                            o("R Range: " + stored.get(0).getTimestamp() + " to " +
                                    stored.get(stored.size() - 1).getTimestamp());
                            o("Q Range: " + calendar.getTime() + " to " + new Date());


                            throw new RuntimeException(
                                    "downloaded values did not contain expected posted value. " + value.toString());


                        }
                    }




            }
        }

        private void verifyCountParam() {
            for (Entity entity : pointList) {
                for (int i = 1; i < 1000; i++) {
                    o("getting " + i + " values for " + entity.getName());
                    List<Value> values = nimbits.getValues(entity, i);
                    if (values.size() != i) {
                        throw new RuntimeException("asked for " + i + " values but got " + values.size());
                    }

                }





            }
        }

    }





    public static boolean containsMask(Value value, String mask) {

        if (StringUtils.isEmpty(mask)) {
            return true;
        }
        else if (! StringUtils.isEmpty(mask) && mask.equals(value.getMetaData()) ) {

            return true;
        }
        else if (! StringUtils.isEmpty(mask)) {
            try {
                Pattern p = Pattern.compile(mask);
                Matcher m = p.matcher(value.getMetaData());
                return m.find();
            }
            catch (PatternSyntaxException ex) {

                return false;
            }


        }

        return false;

    }


    private static void o(String msg) {
        System.out.println(new Date() + "  " + msg);
    }


}
