package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.category.Category;
import com.nimbits.cloudplatform.client.model.category.CategoryFactory;
import com.nimbits.cloudplatform.client.model.category.CategoryModel;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.api.EntityApi;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Empathy Lab
 * User: benjamin
 * Date: 1/7/13
 * Time: 1:24 PM
 */
public class EntityApiTest extends NimbitsServletTest {


    EntityApi impl = new EntityApi();


    public MockHttpServletRequest req1;
    public MockHttpServletResponse resp1;

    @Before
    public void setup() {
        req1 = new MockHttpServletRequest();
        resp1 = new MockHttpServletResponse();
    }

    @Test
    public void testPostDeletePoint() throws IOException, ServletException, Exception {
        req.removeAllParameters();


        req.addParameter("id", point.getKey());
        req.addParameter("action", "delete");
        req.addParameter("type", "point");
        req.setMethod("POST");
        impl.doPost(req, resp);

        List<Entity> r = EntityServiceFactory.getInstance().getEntityByKey(user, point.getKey(), EntityType.point);
        assertTrue(r.isEmpty());


    }

    @Test
    public void testPostCreatePointConflict() throws IOException, ServletException, Exception {
        req.removeAllParameters();
        String pointJson = GsonFactory.getInstance().toJson(point);


        req.addParameter("action", "create");
        req.addParameter("json", pointJson);
        req.setMethod("POST");
        impl.doPost(req, resp);

        assertEquals(HttpServletResponse.SC_CONFLICT, resp.getStatus());
//        List<Entity> r =  EntityServiceImpl.getEntityByKey(user, point.getKey(), EntityType.point);
//        assertTrue(r.isEmpty());


    }
    @Test
    public void testCreateFolderIfMissing() throws IOException, ServletException, Exception {
        req.removeAllParameters();

        Entity e = EntityModelFactory.createEntity("test", "",
                EntityType.category,
                ProtectionLevel.everyone,
                user.getKey(),
                user.getKey());

        Category category = CategoryFactory.createCategory(e);

        String j = GsonFactory.getInstance().toJson(category);

        MockHttpServletRequest req2 = new MockHttpServletRequest();
        MockHttpServletResponse resp2 = new MockHttpServletResponse();

        req.addParameter("action", Action.createmissing.getCode());
        req.addParameter("json", j);
        req.setMethod("POST");
        impl.doPost(req, resp);
        String re = resp.getContentAsString();
        Entity ex = GsonFactory.getInstance().fromJson(re, CategoryModel.class);
        assertNotNull(ex);
        assertEquals(ex.getName(), category.getName());
        assertEquals(resp.getHeader(EntityApi.SERVER_RESPONSE), EntityApi.CREATING_ENTITY);


        req.removeAllParameters();
        req2.addParameter("action", Action.createmissing.getCode());
        req2.addParameter("json", j);
        req2.setMethod("POST");
        impl.doPost(req2, resp2);
        assertEquals(resp2.getHeader(EntityApi.SERVER_RESPONSE), EntityApi.ENTITY_ALREADY_EXISTS);

//        List<Entity> r =  EntityServiceImpl.getEntityByKey(user, point.getKey(), EntityType.point);
//        assertTrue(r.isEmpty());


    }
    @Test
    public void testPostCreatePoint() throws IOException, ServletException, Exception {
        req.removeAllParameters();
        String pointJson = GsonFactory.getInstance().toJson(point);
        EntityServiceFactory.getInstance().deleteEntity(user, Arrays.<Entity>asList(point));
        req.addParameter("action", "create");
        req.addParameter("json", pointJson);
        req.setMethod("POST");
        impl.doPost(req, resp);
        assertEquals(HttpServletResponse.SC_OK, resp.getStatus());


//        List<Entity> r =  EntityServiceImpl.getEntityByKey(user, point.getKey(), EntityType.point);
//        assertTrue(r.isEmpty());


    }

    @Test
    public void testPostUpdatePoint() throws IOException, ServletException, Exception {
        req.removeAllParameters();
        point.setUnit("foo");
        String pointJson = GsonFactory.getInstance().toJson(point);

        req.addParameter("action", "update");
        req.addParameter("json", pointJson);
        req.setMethod("POST");
        impl.doPost(req, resp);
        assertEquals(HttpServletResponse.SC_OK, resp.getStatus());
        List<Entity> sample = EntityServiceFactory.getInstance().getEntityByKey(user, point.getKey(), EntityType.point);
        assertFalse(sample.isEmpty());
        Point p = (Point) sample.get(0);
        assertEquals("foo", p.getUnit());


//        List<Entity> r =  EntityServiceImpl.getEntityByKey(user, point.getKey(), EntityType.point);
//        assertTrue(r.isEmpty());


    }


}


