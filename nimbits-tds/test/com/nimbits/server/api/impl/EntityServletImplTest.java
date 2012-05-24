package com.nimbits.server.api.impl;

import com.google.gwt.benchmarks.client.Setup;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.SubscriptionNotifyMethod;
import com.nimbits.client.enums.SubscriptionType;
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

/**
 * User: benjamin
 * Date: 5/16/12
 * Time: 1:16 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
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
        req.addParameter("json", "{\"highAlarm\":0.0,\"expire\":190,\"unit\":null,\"lowAlarm\":0.0,\"highAlarmOn\":false,\"lowAlarmOn\":true,\"idleAlarmOn\":false,\"idleSeconds\":60,\"idleAlarmSent\":false,\"filterType\":0,\"filterValue\":0.1,\"name\":\"jquery test\",\"description\":\"hello world\",\"entityType\":1,\"protectionLevel\":2,\"alertType\":0,\"parent\":\"bsautner@gmail.com\",\"owner\":\"bsautner@gmail.com\"}");
        req.addParameter("action", "create");
        impl.doPost(req, resp);
        String g1= resp.getContentAsString();
        assertNotNull(g1);
        Point px = GsonFactory.getInstance().fromJson(g1, PointModel.class);
        assertEquals(190, px.getExpire());

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
