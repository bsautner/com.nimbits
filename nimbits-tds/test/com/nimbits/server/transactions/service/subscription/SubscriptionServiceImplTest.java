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
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.subscription.SubscriptionService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.counter.CounterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class SubscriptionServiceImplTest extends NimbitsServletTest {

    @Resource(name="subscriptionService")
    SubscriptionService subscriptionService;

    @Resource(name="entityService")
    EntityService entityService;

    @Resource(name="counterService")
    CounterService counterService;

    @Test
    public void testOkToProcess() throws Exception {


        EntityName name = CommonFactoryLocator.getInstance().createName("sub1", EntityType.subscription);
        Entity entity = EntityModelFactory.createEntity(name, EntityType.subscription);
        Subscription s = SubscriptionFactory.createSubscription(entity, point.getKey(), SubscriptionType.newValue,
                SubscriptionNotifyMethod.email, 2,false, true);
        Subscription result = (Subscription) entityService.addUpdateEntity(s);
        Thread.sleep(3000);
        assertTrue(subscriptionService.okToProcess(result));




    }

    @Test
    public void testOkToProcess2() throws Exception {


        EntityName name = CommonFactoryLocator.getInstance().createName("sub1", EntityType.subscription);
        Entity entity = EntityModelFactory.createEntity(name, EntityType.subscription);
        Subscription s = SubscriptionFactory.createSubscription(entity, point.getKey(), SubscriptionType.newValue,
                SubscriptionNotifyMethod.email, 100,false, true);
        Subscription result = (Subscription) entityService.addUpdateEntity(s);
        counterService.updateDateCounter(result.getKey());

        assertFalse(subscriptionService.okToProcess(result));




    }

    @Test
    public void testLastSentCaching() {



    }



}
