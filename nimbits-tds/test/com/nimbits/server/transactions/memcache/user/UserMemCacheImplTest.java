package com.nimbits.server.transactions.memcache.user;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.user.*;
import helper.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/5/12
 * Time: 3:41 PM
 */
public class UserMemCacheImplTest extends NimbitsServletTest{

    @Test
    public void testCache() throws NimbitsException {

        UserMemCacheImpl impl = new UserMemCacheImpl();
        impl.addUserToCache(user);
        User u = impl.getUserFromCache(user.getEmail());
        assertNotNull(u);
        assertEquals(u.getEmail(), user.getEmail());



    }

}
