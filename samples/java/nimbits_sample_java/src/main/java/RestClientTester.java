import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.io.Nimbits;

import java.util.*;

/**
 * Exercises the V3 Rest API - designed to run continuously against a server as an integration test.
 *
 *
 */
public class RestClientTester {
    private static final String EMAIL_ADDRESS ="test@example.com";
    private static final String INSTANCE_URL = "http://192.168.1.11:8080";
    private static final String PASSWORD = "password1234";
    private static final Nimbits nimbits = new Nimbits(EMAIL_ADDRESS, PASSWORD, INSTANCE_URL);

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
            user = verifyUser();

            //reset the users password to test that
            user.setPassword(UUID.randomUUID().toString());


            if (user != null) {
                o("Continuing with user: " + user.getEmail() + " " + user.getUUID());
                createPoints();
                recordSeriesData();
            }
            else {
                o("User was null! Exiting Test");
            }

            o("Done!");






        }



        /**
         * Try to get my user from the root of the service/v2/rest/me uri - or create it by posting new user credentials to
         * /service/v2/rest
         */

        private User verifyUser() {
            //See if my user id and password get me my user

            try {

                o("Trying to get existing user info");
                return nimbits.getMe();
            } catch (Throwable throwable) {
                //user not found, let's create on - the first user will be an admin of the server
                o("Server returned error - creating user instead " + throwable.getMessage());
                return createUser();

            }
        }

        /**
         * Creates a new user, if this is the first user on the system, it will be the admin
         *
         *
         */
        private User createUser() {

            User postObject = new UserModel(EMAIL_ADDRESS, PASSWORD);
            User newUser = nimbits.addUser(postObject);
            if (newUser == null || ! newUser.getEmail().getValue().equals(EMAIL_ADDRESS)) {
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
            for (Entity entity : pointList) {

                List<Value> values = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                for (int i = 0; i < 1000; i++) {

                    calendar.add(Calendar.SECOND, 1);
                    values.add(ValueFactory.createValueModel(r.nextDouble() * 1000, "{foo:bar}", "Meta Data" + UUID.randomUUID().toString(),
                            calendar.getTime()));
                }
                nimbits.recordValues(entity, values);
                o("Recorded : " + values.size() + " for " + entity.getName());
                storedValues.put(entity, values);

            }

            o("Verifying Data");
            verifySeriesData();

            Thread.sleep(1000);
            o("Verifying Data again!");
            verifySeriesData();


        }

        private void verifySeriesData() {
            for (Entity entity : pointList) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -31);
                o(entity.getUUID());
                List<Value> downloadedValues = nimbits.getValues(entity, calendar.getTime(), new Date());
                o("Downloaded " + downloadedValues.size() + " for " + entity.getName());
                List<Value> stored = storedValues.get(entity);
                Collections.sort(stored);
                Collections.sort(downloadedValues);
                for (Value v : downloadedValues) {
                    o(entity.getName() + " " + v.toString());
                }
                for (Value value : stored) {
                    if (! downloadedValues.contains(value)) {
                        o("R Range: " + stored.get(0).getTimestamp() + " to " +
                                stored.get(stored.size()-1).getTimestamp());
                        o("Q Range: " + calendar.getTime() + " to " + new Date());


                        throw new RuntimeException(
                                "Downloaded Values did not contain stored value.");



                    }
                }
            }
        }

    }




    private static void o(String msg) {
        System.out.println(new Date() + "  " + msg);
    }


}
