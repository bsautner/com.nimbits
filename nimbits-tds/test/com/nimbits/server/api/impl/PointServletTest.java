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

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 9:58 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml"
})
public class PointServletTest extends NimbitsServletTest {

    @Resource(name="pointApi")
    PointServletImpl i;

    @Resource(name="pointService")
    PointService pointService;


    @Test
    public void createPointTest() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("test", EntityType.point);

         Point p = i.createPoint(user, name, null, null, "description sample");
    }
    @Test
    public void createPointDescTest() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("test", EntityType.point);

        Point p = i.createPoint(user, name, null, null, "description sample");

        Entity r = entityService.getEntityByName(user, name, EntityType.point).get(0);
        assertEquals(name.getValue(), r.getName().getValue());
        assertEquals("description sample", r.getDescription());
    }


    @Test
    public void testPostParent() throws NimbitsException {

        req.removeAllParameters();
        req.addParameter("action", "create");
        req.addParameter("point", "parentPoint");
        i.handleRequest(req, resp);
        EntityName name = CommonFactoryLocator.getInstance().createName("parentPoint", EntityType.point);

        List<Entity> result = entityService.getEntityByName(user, name, EntityType.point);
        assertFalse(result.isEmpty());

        req.removeAllParameters();
        req.addParameter("action", "create");
        req.addParameter("point", "child");
        req.addParameter("parent", "parentPoint");
        i.handleRequest(req, resp);
        EntityName name2 = CommonFactoryLocator.getInstance().createName("child", EntityType.point);
        List<Entity> result2 = entityService.getEntityByName(user, name2, EntityType.point);
        assertFalse(result2.isEmpty());

        List<Entity> c = entityService.getChildren(result.get(0), EntityType.point);
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
    public void testGetList() throws NimbitsException {
        pointChild.setProtectionLevel(ProtectionLevel.onlyMe);
        Point p = (Point) entityService.addUpdateEntity(pointChild);
        assertEquals(p.getProtectionLevel(), ProtectionLevel.onlyMe);
        helper.setEnvIsLoggedIn(false);
        req.removeAllParameters();
        req.addParameter(Parameters.action.name(), Action.list.getCode());
        req.addParameter(Parameters.email.getText(), user.getKey());
        req.addParameter(Parameters.key.getText(), "AUTH");
        req.addParameter(Parameters.point.getText(), point.getName().getValue());
        String r = i.processGet(req, resp);
        assertNotNull(r);
        assertTrue(r.length() > 0);


     }
    @Test
    public void testGetListFail() throws NimbitsException {
        pointChild.setProtectionLevel(ProtectionLevel.onlyMe);
        Point p = (Point) entityService.addUpdateEntity(pointChild);
        assertEquals(p.getProtectionLevel(), ProtectionLevel.onlyMe);
        helper.setEnvIsLoggedIn(false);
        req.removeAllParameters();
        req.addParameter(Parameters.action.name(), Action.list.getCode());
        req.addParameter(Parameters.email.getText(), user.getKey());
        req.addParameter(Parameters.key.getText(), "AUTHX");
        req.addParameter(Parameters.point.getText(), point.getName().getValue());
        String r = i.processGet(req, resp);
        assertNotNull(r);
        assertTrue(r.length() == 0);


    }
    @Test
    public void testGetListSharedNoKey() throws NimbitsException {
        pointChild.setProtectionLevel(ProtectionLevel.everyone);
        Point p = (Point) entityService.addUpdateEntity(pointChild);
        assertEquals(p.getProtectionLevel(), ProtectionLevel.everyone);
        helper.setEnvIsLoggedIn(false);
        req.removeAllParameters();
        req.addParameter(Parameters.action.name(), Action.list.getCode());
        req.addParameter(Parameters.email.getText(), user.getKey());
       // req.addParameter(Parameters.key.getText(), "AUTHX");
        req.addParameter(Parameters.point.getText(), point.getName().getValue());
        String r = i.processGet(req, resp);
        assertNotNull(r);
        assertTrue(r.length() > 0);


    }

    @Test
    public void testCreatePointWithPost() throws UnsupportedEncodingException {
        req.removeAllParameters();
        req.addParameter("point", "Created");
        req.addParameter("action", Action.create.name());
        i.handleRequest(req, resp);

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
        entityService.addUpdateEntity(point);
        List<Entity> rl = entityService.getEntityByKey(user, point.getKey(), EntityType.point);
        assertFalse(rl.isEmpty());
        Point rp = (Point)rl.get(0);

        assertEquals(rp.getProtectionLevel(), ProtectionLevel.everyone);

        req.removeAllParameters();
        req.addParameter(Parameters.uuid.getText(), point.getKey());
        req.addParameter(Parameters.email.getText(), user.getKey());

        helper.setEnvIsLoggedIn(false);

        String r = i.processGet(req, resp);
        Point ret = GsonFactory.getInstance().fromJson(r, PointModel.class);
        assertNotNull(ret);



    }
    @Test
    public void testGetNotLoggedInAccessProtected() throws NimbitsException {

        point.setProtectionLevel(ProtectionLevel.onlyMe);
        entityService.addUpdateEntity(point);
        List<Entity> rl = entityService.getEntityByKey(user, point.getKey(), EntityType.point);
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

//    @Test
//    public void getObjTest() throws NimbitsException {
//        PointServletImpl.doInit(req, resp, ExportType.plain);
//        String g = PointServletImpl.getPointObjects(group.getName().getValue(), null);
//        List<Point> r = GsonFactory.getInstance().fromJson(g, GsonFactory.pointListType);
//        assertFalse(r.isEmpty());
//         Point px = r.get(0);
//        assertNotNull(px.getFilterType());
//        String p = PointServletImpl.getPointObjects(null, point.getName().getValue());
//        Point pr = GsonFactory.getInstance().fromJson(p, PointModel.class);
//        assertNotNull(pr);
//
//    }

}
