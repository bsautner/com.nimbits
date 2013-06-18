package com.nimbits.cloudplatform.server.transaction;

import com.google.appengine.tools.development.testing.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 3/11/13
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchServiceImplTest {


    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
            new LocalUserServiceTestConfig())
            .setEnvIsLoggedIn(true).setEnvEmail("test@example.com").setEnvAuthDomain("nimbits.com");


    @Before
    public void setUp()  {

        helper.setUp();
    }
    @After
    public void tearDown() {
        helper.tearDown();

    }

    @Test
    public void testSearch() throws Exception {
         new SearchServiceImpl().search();
    }
}
