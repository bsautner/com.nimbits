package com.nimbits.server;

import com.google.appengine.api.utils.SystemProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by benjamin on 10/11/13.
 */
public class ServerSettingTest {
//    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
//            new LocalDatastoreServiceTestConfig(),
//            new LocalTaskQueueTestConfig(),
//            new LocalBlobstoreServiceTestConfig(),
//            new LocalUserServiceTestConfig())
//            .setEnvIsLoggedIn(true).setEnvEmail("test@example.com").setEnvAuthDomain("nimbits.com");


    @Before
    public void setUp() throws Exception {
        SystemProperty.environment.set(SystemProperty.Environment.Value.Development);

        //helper.setUp();

    }

    @Test
    public void testSave() {
        assertTrue(true);
        ServiceTest test = new ServiceTest();
        test.writeToDb();
    }

}
