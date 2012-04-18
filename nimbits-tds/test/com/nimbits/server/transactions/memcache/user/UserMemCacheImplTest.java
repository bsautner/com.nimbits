package com.nimbits.server.transactions.memcache.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.user.User;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/5/12
 * Time: 3:41 PM
 */
public class UserMemCacheImplTest extends NimbitsServletTest {

    @Test
    public void testCache() throws NimbitsException {

        UserMemCacheImpl impl = new UserMemCacheImpl();
        impl.addUserToCache(user);
        User u = impl.getUserFromCache(user.getEmail());
        assertNotNull(u);
        assertEquals(u.getEmail(), user.getEmail());



    }

}
