package com.nimbits.it;

import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.After;
import org.junit.Before;

import java.util.Date;

/**
 * A base class for running tests
 */

public abstract class NimbitsTest {

    public User user;

    static final String EMAIL_ADDRESS = "support@nimbits.com";
    // static final String INSTANCE_URL = "http://52.87.221.104:8080/nimbits";
    //static final String INSTANCE_URL = "http://ha:8080/nimbits";

    static final String INSTANCE_URL = "http://localhost:8080";
    static final String PASSWORD = "password"; //just like my luggage!
    int errors = 0;


    static final Nimbits nimbits = new Nimbits.Builder()
            .email(EMAIL_ADDRESS).token(PASSWORD).instance(INSTANCE_URL).create();



    @Before
    public void setUp() throws Exception {
        user = verifyAdminUser();

    }

    @After
    public void tearDown() throws Exception {

//        List<Entity> entityList = nimbits.getChildren(user);
//        for (Entity entity : entityList) {
//            log("teardown deletinging child: " + entity.getName().getValue());
//             nimbits.deleteEntity(entity);
//        }


    }

    static void log(Object... msg) {
        StringBuilder sb = new StringBuilder();
        for (Object o : msg) {
            sb.append("-> ").append(String.valueOf(o)).append("\n");
        }
        System.out.println(String.format("%s %s", new Date(), sb.toString()));
    }



    void sleep() {
        try {
            log("sleeping...");
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {

        }
    }

    void sleep(int i) {
        log("big sleep " + i);
        for (int c = 0; c < i; c++) {
            sleep();
        }
    }


    private User verifyAdminUser() {
        //See if my user id and password get me my user
        User user;

        try {

            log("Trying to get existing user");
            user = nimbits.getMe(true);
        } catch (Throwable throwable) {
            //user not found, let's create on - the first user will be an admin of the server
            log("Server returned error - creating user instead " + throwable.getMessage());
            String s = ExceptionUtils.getStackTrace(throwable);
            log(s);
            throwable.printStackTrace();
            user = createUser(EMAIL_ADDRESS, PASSWORD);

        }

        log("Got User:" + user.toString());


        return nimbits.getMe(true);


    }

    /**
     * Creates a new user, if this is the first user on the system, it will be the admin
     */
    User createUser(String email, String password) {

        User postObject = new UserModel.Builder().email(email).password(password).create();
        User newUser = nimbits.addUser(postObject);
        if (newUser == null || !newUser.getEmail().getValue().equals(email)) {
            throw new RuntimeException("Could not create a new user");
        } else {
            log("Created new user: " + newUser.getEmail());
            return newUser;
        }
    }

    void error(String message) {
        log("Error: " + message);
        errors++;
        throw new RuntimeException(message);
    }
}
