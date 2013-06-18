package com.nimbits.server.json;

import org.junit.*;
import static org.junit.Assert.assertFalse;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/19/12
 * Time: 5:05 PM
 */
public class JsonHelperTest {

    @Test
    public void testWord() {

        assertFalse(JsonHelper.isJson("hey"));
    }
}
