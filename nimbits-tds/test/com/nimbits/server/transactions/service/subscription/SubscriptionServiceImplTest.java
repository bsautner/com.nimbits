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

package com.nimbits.server.transactions.service.subscription;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionFactory;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SubscriptionServiceImplTest extends NimbitsServletTest {
    @Test
    public void testOkToProcess() throws Exception {

        Calendar lastSent = Calendar.getInstance();
        lastSent.add(Calendar.DATE, -5);
        EntityName name = CommonFactoryLocator.getInstance().createName("sub1", EntityType.subscription);
        Entity entity = EntityModelFactory.createEntity(name, EntityType.subscription);
        Subscription s = SubscriptionFactory.createSubscription(entity, point.getKey(), SubscriptionType.newValue,
                SubscriptionNotifyMethod.email, 5.0,lastSent.getTime(),false, true);
        SubscriptionServiceImpl i = new SubscriptionServiceImpl();
        assertTrue(i.okToProcess(s));




    }

    @Test
    public void testOkToProcess2() throws Exception {

        Calendar lastSent = Calendar.getInstance();
        //lastSent.add(Calendar.DATE, -5);
        EntityName name = CommonFactoryLocator.getInstance().createName("sub1", EntityType.subscription);
        Entity entity = EntityModelFactory.createEntity(name, EntityType.subscription);
        Subscription s = SubscriptionFactory.createSubscription(entity, point.getKey(), SubscriptionType.newValue,
                SubscriptionNotifyMethod.email, 15.0,lastSent.getTime(),false, true);
        SubscriptionServiceImpl i = new SubscriptionServiceImpl();
        assertFalse(i.okToProcess(s));




    }
}
