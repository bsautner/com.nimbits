package com.nimbits.client.model.common.impl;

import com.nimbits.client.exception.NimbitsException;

import com.nimbits.client.model.email.EmailAddress;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class CommonFactoryImplTest {
    @Test(expected = NimbitsException.class)
    public void testCreateEmailAddress() throws Exception {
        CommonFactory.emailTest("b");
    }

    @Test(expected = NimbitsException.class)
    public void testCreateEmailAddress2() throws Exception {
        CommonFactory.emailTest("b.com");
    }

    @Test
    public void testCreateEmailAddress3() throws NimbitsException {
        EmailAddress e = CommonFactory.createEmailAddress("b@b.com");
        assertNotNull(e);
        assertEquals("b@b.com", e.getValue());
    }
}
