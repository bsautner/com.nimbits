package com.nimbits.it.admin;


import com.google.common.base.Optional;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;


public class UserAdminCrud extends AbstractBaseNimbitsTest {
    private final static String email_format = "%s@example.com";

    @Test
    public void getUserTest() {

        String testEmail =  String.format(email_format, UUID.randomUUID().toString());


        adminClient.addUser(new UserModel.Builder().email(testEmail).password("password").create());

        nap();
        Optional<User> userOptional = adminClient.getUser(testEmail);

       assertTrue(userOptional.isPresent());



    }

   // @Test(expected = RetrofitError.class)
    public void UserNotFoundTest() {

        String testEmail =  String.format(email_format, UUID.randomUUID().toString());

        adminClient.getUser(testEmail);



    }

   // @Test(expected = RetrofitError.class)
    public void deleteUserTest() {

        String testEmail =  String.format(email_format, UUID.randomUUID().toString());


        adminClient.addUser(new UserModel.Builder().email(testEmail).password("password2").create());


        User user = adminClient.getUser(testEmail).get();

        assertNotNull(user);
        assertNotNull(user.getId());
        assertFalse(user.getIsAdmin());

        adminClient.deleteUser(testEmail);

        nap();

        adminClient.getUser(testEmail);




    }

}
