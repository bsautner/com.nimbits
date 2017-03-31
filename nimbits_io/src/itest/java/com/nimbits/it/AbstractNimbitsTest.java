package com.nimbits.it;

import com.google.common.base.Optional;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.it.basic.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * A base class for running tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationTestContext.xml"})
public abstract class AbstractNimbitsTest extends AbstractTest {

    public User user;

    @Value("${test.admin}")
    private String admin;

    @Value("${test.password}")
    public String password;

    @Value("${test.host}")
    public String host;



     public Nimbits nimbits;



    @Before
    public void setUp() throws Exception {
         nimbits = new Nimbits.Builder()
                .email(admin).token(password).instance(host).create();
        Optional<User> userOptional = nimbits.getMe(false);

        if (userOptional.isPresent()) {
            List<Entity> entityList = nimbits.getChildren(user);
            for (Entity entity : entityList) {
                log("teardown deleting child: " + entity.getName().getValue());
                nimbits.deleteEntity(entity);
            }
        } else {
            fail("could not find admin user");
        }

    }

    @After
    public void tearDown() throws Exception {




    }



    /**
     * Creates a new user, if this is the first user on the system, it will be the admin
     */
    public User createUser(String email, String password) {

        User postObject = new UserModel.Builder().email(email).password(password).create();
        User newUser = nimbits.addUser(postObject);
        if (newUser == null || !newUser.getEmail().getValue().equals(email)) {
            throw new RuntimeException("Could not create a new user");
        } else {
            log("Created new user: " + newUser.getEmail());
            return newUser;
        }
    }


}
