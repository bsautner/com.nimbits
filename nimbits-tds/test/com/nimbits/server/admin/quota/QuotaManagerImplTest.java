/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.admin.quota;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 10/16/12
 * Time: 10:24 AM
 */
public class QuotaManagerImplTest extends NimbitsServletTest {
    @Test
    public void testUpdateUserStatusGrid() throws Exception {
       quotaManager.updateUserStatusGrid(user, 100);
       Map<EmailAddress, User> map = quotaManager.getUserStatusGrid();
       assertNotNull(map);
       assertEquals(1, map.size());

        quotaManager.updateUserStatusGrid(user, 101);
        Map<EmailAddress, User> map2 = quotaManager.getUserStatusGrid();
        assertNotNull(map2);
        assertEquals(1, map2.size());
        User ur = map2.get(user.getEmail());
        assertEquals(101, ur.getApiCount());

    }



    @Test
    public void testGetFreeDailyQuota() throws Exception {

    }

    @Test
    public void testIncrementCounter() throws Exception {

    }

    @Test
    public void testGetCostPerApiCall() throws Exception {

    }

    @Test
    public void testGetCount() throws Exception {

    }

    @Test
    public void testSetCacheFactory() throws Exception {

    }

    @Test
    public void testSetTimespanService() throws Exception {

    }
}
