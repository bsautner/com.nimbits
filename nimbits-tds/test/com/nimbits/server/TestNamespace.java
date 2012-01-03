package com.nimbits.server;

import com.nimbits.client.model.*;
import com.nimbits.server.memcache.*;
import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/22/11
 * Time: 1:04 PM
 */
public class TestNamespace {
    @Test

    public void testName() {
        String s = Const.REGEX_NAMESPACE;
        String s1 = "testexample.com";
        String s2 = "test@example.com";
        assertTrue(s1.matches(s));
        assertFalse(s2.matches(s));
        assertTrue(MemCacheHelper.makeSafeNamespace(s2).matches(s));
    }

}
