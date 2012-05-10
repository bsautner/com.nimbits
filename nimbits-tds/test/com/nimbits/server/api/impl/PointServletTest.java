package com.nimbits.server.api.impl;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.transactions.service.entity.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 9:58 AM
 */
public class PointServletTest extends NimbitsServletTest {
    PointServletImpl i = new PointServletImpl();
    @Test
    public void createPointTest() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("test", EntityType.point);

         Point p = PointServletImpl.createPoint(user, name, null, null, "description sample");
    }
    @Test
    public void createPointDescTest() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("test", EntityType.point);

        Point p = PointServletImpl.createPoint(user, name, null, null, "description sample");

        Entity r = EntityServiceFactory.getInstance().getEntityByName(user, name, EntityType.point).get(0);
        assertEquals(name.getValue(), r.getName().getValue());
        assertEquals("description sample", r.getDescription());
    }


    @Test
    public void testPostParent() throws NimbitsException {

        req.removeAllParameters();
        req.addParameter("action", "create");
        req.addParameter("point", "parentPoint");
        i.doPost(req, resp);
        EntityName name = CommonFactoryLocator.getInstance().createName("parentPoint", EntityType.point);

        List<Entity> result = EntityServiceFactory.getInstance().getEntityByName(user, name, EntityType.point);
        assertFalse(result.isEmpty());

        req.removeAllParameters();
        req.addParameter("action", "create");
        req.addParameter("point", "child");
        req.addParameter("parent", "parentPoint");
        i.doPost(req, resp);
        EntityName name2 = CommonFactoryLocator.getInstance().createName("child", EntityType.point);
        List<Entity> result2 = EntityServiceFactory.getInstance().getEntityByName(user, name2, EntityType.point);
        assertFalse(result2.isEmpty());

        List<Entity> c = EntityServiceFactory.getInstance().getChildren(result.get(0), EntityType.point);
        assertFalse(c.isEmpty());
        assertEquals(result2.get(0), c.get(0));


    }

    @Test
    public void testGet() {

        req.addParameter(Parameters.uuid.getText(), group.getKey());

        String r = i.processGet(req, resp);
        Category c = GsonFactory.getInstance().fromJson(r, CategoryModel.class);
        Assert.assertFalse(c.getChildren().isEmpty());



    }


    @Test
    public void testCreatePointWithPost() throws UnsupportedEncodingException {
        req.removeAllParameters();
        req.addParameter("point", "Created");
        req.addParameter("action", Action.create.name());
        i.doPost(req, resp);

        req.removeAllParameters();
        req.addParameter("point", "Created");
        req.addParameter("format", "json");
        i.doGet(req, resp);
        String r = resp.getContentAsString();
        assertNotNull(r);


    }

    @Test
    public void testPointExists() throws UnsupportedEncodingException {
        req.removeAllParameters();
        req.addParameter("point", pointName.getValue());
        req.addParameter("action", Action.validateExists.getCode());
        i.doGet(req, resp);
        String r = resp.getContentAsString();
        assertTrue(Boolean.valueOf(r));
    }
    @Test
    public void testPointDoesNotExist() throws UnsupportedEncodingException {
        req.removeAllParameters();
        req.addParameter("point","IDONTEXIST1223");
        req.addParameter("action", Action.validateExists.getCode());
        i.doGet(req, resp);
        String r = resp.getContentAsString();
        assertFalse(Boolean.valueOf(r));
    }

    @Test
    public void testGetNotLoggedIn() throws NimbitsException {

        point.setProtectionLevel(ProtectionLevel.everyone);
        EntityServiceFactory.getInstance().addUpdateEntity(point);
        List<Entity> rl = EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), EntityType.point);
        assertFalse(rl.isEmpty());
        Point rp = (Point)rl.get(0);

        assertEquals(rp.getProtectionLevel(), ProtectionLevel.everyone);

        req.removeAllParameters();
        req.addParameter(Parameters.uuid.getText(), point.getKey());

        helper.setEnvIsLoggedIn(false);

        String r = i.processGet(req, resp);
        Point ret = GsonFactory.getInstance().fromJson(r, PointModel.class);
        assertNotNull(ret);



    }
    @Test
    public void testGetNotLoggedInAccessProtected() throws NimbitsException {

        point.setProtectionLevel(ProtectionLevel.onlyMe);
        EntityServiceFactory.getInstance().addUpdateEntity(point);
        List<Entity> rl = EntityServiceFactory.getInstance().getEntityByKey(point.getKey(), EntityType.point);
        assertFalse(rl.isEmpty());
        Point rp = (Point)rl.get(0);

        assertEquals(rp.getProtectionLevel(), ProtectionLevel.onlyMe);

        req.removeAllParameters();
        req.addParameter(Parameters.uuid.getText(), point.getKey());

        helper.setEnvIsLoggedIn(false);

        String r = i.processGet(req, resp);
        Point ret = GsonFactory.getInstance().fromJson(r, PointModel.class);
        assertNull(ret);



    }

    @Test
    public void getObjTest() throws NimbitsException {
        PointServletImpl.doInit(req, resp, ExportType.plain);
        String g = PointServletImpl.getPointObjects(group.getName().getValue(), null);
        List<Point> r = GsonFactory.getInstance().fromJson(g, GsonFactory.pointListType);
        assertFalse(r.isEmpty());
         Point px = r.get(0);
        assertNotNull(px.getFilterType());
        String p = PointServletImpl.getPointObjects(null, point.getName().getValue());
        Point pr = GsonFactory.getInstance().fromJson(p, PointModel.class);
        assertNotNull(pr);

    }

}
