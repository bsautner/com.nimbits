package com.nimbits.server;

import com.google.appengine.api.utils.SystemProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by benjamin on 10/11/13.
 */
public class ServerSettingTest {


    @Before
    public void setUp() throws Exception {
        SystemProperty.environment.set(SystemProperty.Environment.Value.Development);



    }

    @Test
    public void testSave() {
        assertTrue(true);
        ServiceTest test = new ServiceTest();
        test.writeToDb();
    }

}
