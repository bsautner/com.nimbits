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

package com.nimbits.server.api.impl;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionFactory;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntityServletImplTest extends NimbitsServletTest {
    EntityServletImpl impl = new EntityServletImpl();
    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;

    @Before
    public void setup() {
            req1 = new MockHttpServletRequest();
            resp1 = new MockHttpServletResponse();
    }
    @Test
    public void testPost() throws IOException, ServletException, NimbitsException {
        req.removeAllParameters();


        req.addParameter("id", point.getKey());


        impl.doGet(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Point px = GsonFactory.getInstance().fromJson(g1, PointModel.class);
        assertEquals(point.getExpire(), px.getExpire());

    }
    @Test
    public void testPostCreatePoint() throws IOException, ServletException, NimbitsException {
        req.removeAllParameters();


      //  req.addParameter("id", point.getKey());
        req.addParameter("json", "{\"highAlarm\":0.0,\"expire\":190,\"unit\":null,\"lowAlarm\":0.0,\"highAlarmOn\":false,\"lowAlarmOn\":true,\"idleAlarmOn\":false,\"idleSeconds\":60,\"idleAlarmSent\":false,\"filterType\":0,\"filterValue\":0.1,\"name\":\"jquery test\",\"description\":\"hello world\",\"entityType\":1,\"protectionLevel\":2,\"alertType\":0,\"parent\":\"" + Const.TEST_ACCOUNT + "\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}");
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Point px = GsonFactory.getInstance().fromJson(g1, PointModel.class);
        assertEquals(190, px.getExpire());

    }
    @Test
    public void testPostDeletePoint() throws IOException, ServletException, NimbitsException {
        req.removeAllParameters();


        //  req.addParameter("id", point.getKey());
        req.addParameter("json", "{\"highAlarm\":0.0,\"expire\":190,\"unit\":null,\"lowAlarm\":0.0,\"highAlarmOn\":false,\"lowAlarmOn\":true,\"idleAlarmOn\":false,\"idleSeconds\":60,\"idleAlarmSent\":false,\"filterType\":0,\"filterValue\":0.1,\"name\":\"jquery test\",\"description\":\"hello world\",\"entityType\":1,\"protectionLevel\":2,\"alertType\":0,\"parent\":\"" + Const.TEST_ACCOUNT + "\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}");
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Point px = GsonFactory.getInstance().fromJson(g1, PointModel.class);
        assertEquals(190, px.getExpire());
        assertNotNull(px.getKey());
        String j2 = GsonFactory.getInstance().toJson(px);

        req.removeAllParameters();
        req.addParameter("json", j2);
        req.addParameter("action", "delete");
        impl.doPost(req, resp);


    }


    @Test
    public void testMin() throws IOException, ServletException {
        req.removeAllParameters();
        String json = "{\"filterType\":4,\"name\":\"bug2\",\"entityType\":1,\"protectionLevel\":2,\"parent\":\"" + Const.TEST_ACCOUNT + "\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}";
        req.addParameter("json",json);
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Point px = GsonFactory.getInstance().fromJson(g1, PointModel.class);


    }

    @Test
    public void testUpdatePoint() throws IOException, ServletException, NimbitsException {
        req.removeAllParameters();


        //  req.addParameter("id", point.getKey());
        req.addParameter("json", "{\"highAlarm\":0.0,\"expire\":190,\"unit\":null,\"lowAlarm\":0.0,\"highAlarmOn\":false,\"lowAlarmOn\":true,\"idleAlarmOn\":false,\"idleSeconds\":60,\"idleAlarmSent\":false,\"filterType\":0,\"filterValue\":0.1,\"name\":\"jquery test\",\"description\":\"hello world\",\"entityType\":1,\"protectionLevel\":2,\"alertType\":0,\"parent\":\"" + Const.TEST_ACCOUNT + "\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}");
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Point px = GsonFactory.getInstance().fromJson(g1, PointModel.class);
        assertEquals(190, px.getExpire());
        req.removeAllParameters();
        req.addParameter("json", "{\"key\":\"" + px.getKey() + "\",\"highAlarm\":0.0,\"expire\":55,\"unit\":null,\"lowAlarm\":0.0,\"highAlarmOn\":false,\"lowAlarmOn\":true,\"idleAlarmOn\":false,\"idleSeconds\":60,\"idleAlarmSent\":false,\"filterType\":0,\"filterValue\":0.1,\"name\":\"jquery test\",\"description\":\"hello world\",\"entityType\":1,\"protectionLevel\":2,\"alertType\":0,\"parent\":\"" + Const.TEST_ACCOUNT + "\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}");
        req.addParameter("action", "update");
        impl.doPost(req, resp1);

        String g2= resp1.getContentAsString();
        assertNotNull(g2);
        Point p2 = GsonFactory.getInstance().fromJson(g2, PointModel.class);
        assertEquals(55, p2.getExpire());

    }

    @Test  //nimbits does not like incomplete json
    public void testUpdatePointWithFragment() throws IOException, ServletException, NimbitsException {
        req.removeAllParameters();


        //  req.addParameter("id", point.getKey());
        req.addParameter("json", "{\"highAlarm\":0.0,\"expire\":190,\"unit\":null,\"lowAlarm\":0.0,\"highAlarmOn\":false,\"lowAlarmOn\":true,\"idleAlarmOn\":false,\"idleSeconds\":600,\"idleAlarmSent\":false,\"filterType\":0,\"filterValue\":0.1,\"name\":\"jquery test\",\"description\":\"hello world\",\"entityType\":1,\"protectionLevel\":2,\"alertType\":0,\"parent\":\"" + Const.TEST_ACCOUNT + "\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}");
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Point px = GsonFactory.getInstance().fromJson(g1, PointModel.class);
        assertEquals(190, px.getExpire());
        req.removeAllParameters();
        req.addParameter("json", "{\"key\":\"" + px.getKey() + "\",\"entityType\":1,\"expire\":55,\"name\":\"jquery test\",\"parent\":\"" + Const.TEST_ACCOUNT + "\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}");
        req.addParameter("action", "update");
        impl.doPost(req, resp1);

        String g2= resp1.getContentAsString();
        assertNotNull(g2);
        Point p2 = GsonFactory.getInstance().fromJson(g2, PointModel.class);
        assertEquals(55, p2.getExpire());
        assertEquals(0, p2.getIdleSeconds());
    }


    @Test
    public void testPostCreateSubscription() throws IOException, ServletException, NimbitsException {
        req.removeAllParameters();


        //  req.addParameter("id", point.getKey());
        req.addParameter("json", "{\"subscribedEntity\":\"" + Const.TEST_ACCOUNT + "/TempF\",\"notifyMethod\":0,\"subscriptionType\":4,\"maxRepeat\":15.0,\"lastSent\":\"2012-05-20T23:59:37 +0000\",\"notifyFormatJson\":false,\"enabled\":true,\"name\":\"TempF idle alert\",\"key\":\"b9ba6396-b3c8-4455-8744-334f3a2633b0\",\"description\":\"\",\"entityType\":5,\"protectionLevel\":0,\"alertType\":1,\"parent\":\"" + Const.TEST_ACCOUNT + "/TempF\",\"owner\":\"" + Const.TEST_ACCOUNT + "\"}");
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Subscription px = GsonFactory.getInstance().fromJson(g1, SubscriptionModel.class);
        assertEquals(px.getSubscriptionType(), SubscriptionType.idle);

    }

    @Test
    public void testSubscribe() throws IOException, ServletException, NimbitsException {

        req.removeAllParameters();
        req.addParameter("id", point.getKey());

        EntityName name = CommonFactoryLocator.getInstance().createName("sub1", EntityType.subscription);
        Entity se = EntityModelFactory.createEntity(name, "", EntityType.subscription, ProtectionLevel.onlyConnection,
                point.getKey(), user.getKey());

        Subscription s = SubscriptionFactory.createSubscription(
                se,
                point.getKey(),
                SubscriptionType.high,
                SubscriptionNotifyMethod.email, 5.0, new Date(), false, true);

        String jp = GsonFactory.getInstance().toJson(s);
        System.out.println(jp);

        req.removeAllParameters();

        req.addParameter("json", jp);
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String r = resp.getContentAsString();
        System.out.println(r);

        Subscription sr = GsonFactory.getInstance().fromJson(r, SubscriptionModel.class);
        assertNotNull(sr.getKey());
        assertEquals(s.getName(), sr.getName());



    }

    @Test
    public void testUpdate() throws IOException, ServletException, NimbitsException {

        req.removeAllParameters();
        req.addParameter("id", point.getKey());
        impl.doGet(req, resp);

        String j = resp.getContentAsString();
        Point p = GsonFactory.getInstance().fromJson(j, PointModel.class);
        assertNotNull(p);
        p.setDescription("foo");
        String u = GsonFactory.getInstance().toJson(p, PointModel.class);
        req.removeAllParameters();
        req.addParameter("json", u);
        req.addParameter("action", "update");
        impl.doPost(req, resp);


        req1.removeAllParameters();
        req1.addParameter("id", p.getKey());
        impl.doGet(req1, resp1);
        String x = resp1.getContentAsString();
        Point xpr = GsonFactory.getInstance().fromJson(x, PointModel.class);
        assertNotNull(xpr);
        assertEquals("foo", xpr.getDescription());
 }
    @Test
    public void testUpdate2() throws IOException, ServletException, NimbitsException {



        point.setDescription("foo");
        String u = GsonFactory.getInstance().toJson(point, PointModel.class);
        req.removeAllParameters();
        req.addParameter("json", u);
        req.addParameter("action", "update");
        impl.doPost(req, resp);
        String x = resp.getContentAsString();
        Point xp = GsonFactory.getInstance().fromJson(x, PointModel.class);
        assertNotNull(xp);
        assertEquals("foo", xp.getDescription());



    }

}
