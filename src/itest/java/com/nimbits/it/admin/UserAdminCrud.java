package com.nimbits.it.admin;


import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.model.user.User;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;


public class UserAdminCrud extends AbstractBaseNimbitsTest {
    private final static String email_format = "%s@example.com";

    @Test
    public void getUserTest() {

        String testEmail =  String.format(email_format, UUID.randomUUID().toString());


        adminClient.addUser(new User.Builder().email(testEmail).password("password1").create());

        nap();
        User user = adminClient.getUser(testEmail);

        assertNotNull(user);



    }

    @Test(expected = NimbitsClientException.class)
    public void UserNotFoundTest() {

        String testEmail =  String.format(email_format, UUID.randomUUID().toString());

        adminClient.getUser(testEmail);



    }

    @Test(expected = NimbitsClientException.class)
    public void deleteUserTest() {

        String testEmail =  String.format(email_format, UUID.randomUUID().toString());


        adminClient.addUser(new User.Builder().email(testEmail).password("password2").create());


        User user = adminClient.getUser(testEmail);

        assertNotNull(user);
        assertNotNull(user.getId());


        adminClient.deleteUser(testEmail);

        nap();

        adminClient.getUser(testEmail);




    }

}
