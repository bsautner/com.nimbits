package com.nimbits.it;

import com.google.common.base.Optional;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.user.User;
import com.nimbits.it.basic.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A base class for running tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationTestContext.xml"})
public abstract class AbstractBaseNimbitsTest extends AbstractTest {

    public User adminUser;

    @Value("${test.admin}")
    private String admin;

    @Value("${test.password}")
    private String password;

    @Value("${test.host}")
    public String host;

    public Nimbits adminClient;

    @Before
    public void setUp() throws Exception {
        assertNotNull(admin);
        assertNotNull(password);

       log("Test Setup");
       log(admin);
       log(password);
       adminClient = new Nimbits.Builder()
                .email(admin).token(password).instance(host).create();

       Optional<User> userOptional = adminClient.getMe();
       assertTrue(userOptional.isPresent());
       if (userOptional.isPresent()) {
           adminUser = userOptional.get();
           if (!adminUser.getIsAdmin()) {
               fail("Admin User not set correctly for these tests to run");
           }
       }

    }

    @After
    public void tearDown() throws Exception {


//        List<Entity> tree = adminClient.getChildren(adminUser);
//        for (Entity e : tree) {
//            log("Teardown", e);
//            adminClient.deleteEntity(e);
//
//        }


    }

    protected static void log(Object... msg) {
        StringBuilder sb = new StringBuilder();
        for (Object o : msg) {
            sb.append("-> ").append(String.valueOf(o)).append("\n");
        }
        System.out.println(String.format("%s %s", new Date(), sb.toString()));
    }



    protected void sleep() {
        try {
            log("sleeping...");
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {

        }
    }

    protected void nap() {
        try {
            log("napping...");
            Thread.sleep(500);
        } catch (InterruptedException ignored) {

        }
    }

    protected void sleep(int i) {
        log("big sleep " + i);
        for (int c = 0; c < i; c++) {
            sleep();
        }
    }

    public void error(String message) {
        log("Error: " + message);

        throw new RuntimeException(message);
    }
}
