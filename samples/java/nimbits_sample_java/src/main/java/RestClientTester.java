import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.io.Nimbits;

import java.util.Date;

/**
 * Exercises the V3 Rest API - designed to run continuously against a server as an integration test.
 *
 *
 */
public class RestClientTester {
    private static final String EMAIL_ADDRESS ="test@example.com";
    private static final String INSTANCE_URL = "http://localhost:8888";
    private static final String PASSWORD = "password1234";
    private static final Nimbits nimbits = new Nimbits(EMAIL_ADDRESS, PASSWORD, INSTANCE_URL);

    public static void main(String[] args) {


        o("Starting up");

        NimbitsLoadTester loadTester = new NimbitsLoadTester();
        loadTester.execute();


    }

    private static class NimbitsLoadTester {
        private User user;


        public void execute() {
            user = verifyUser();
            if (user != null) {
                o("Continuing with user: " + user.getEmail() + " " + user.getUUID());
                createPoints();
            }
            else {
                o("User was null! Exiting Test");
            }

        }

        /**
         * Create some data points with the new user as the parent
         */
        private void createPoints() {

            for (int i = 0; i < 10; i++) {
                Point point = new PointModel(user, "point " + i);
                nimbits.addEntity(user, point);
            }

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
                return createUser(throwable);

            }
        }

        /**
         * Creates a new user, if this is the first user on the system, it will be the admin
         *
         * @param throwable
         */
        private User createUser(Throwable throwable) {
            o(throwable.getMessage());
            User postObject = new UserModel(EMAIL_ADDRESS, PASSWORD);
            User newUser = nimbits.addUser(postObject);
            if (newUser == null || newUser.getEmail().getValue().equals(EMAIL_ADDRESS)) {

                throw new RuntimeException("Could not create a new user");
            }
            else {
                o("Create new user" + newUser.getEmail());
                return newUser;
            }
        }

    }




    private static void o(String msg) {
        System.out.println(new Date() + "  " + msg);
    }


}
