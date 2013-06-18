package com.nimbits;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 1:07 PM
 */
public class ServerUtilsTest {


    @Test
    public void  loadRandomDataToTestAccountTest() {

        try {
            ServerUtils.loadRandomDataToTestAccount();
        } catch (NimbitsException e) {
            fail();
            e.printStackTrace();
        }

    }
}
