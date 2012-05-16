package com.nimbits.server.api.impl;

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
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/16/12
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityServletImplTest extends NimbitsServletTest {
    EntityServletImpl impl = new EntityServletImpl();

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

        req.addParameter("id", point.getKey());
       impl.doPost(req, resp);
    }




}
