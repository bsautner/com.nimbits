/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.service.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.helper.EntityHelper;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 2:27 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml"
})
public class EntityServiceTest extends NimbitsServletTest {

    @Resource(name="entityHelper")
    private EntityHelper entityHelper;

    @Test
    public void addUpdateEntity() throws NimbitsException {
        Point p = entityHelper.createPointWithName(UUID.randomUUID().toString());
        Point r = (Point) entityService.addUpdateEntity(p);
        assertNotNull(p);
        assertNotNull(r);

    }


    @Test
    public void deleteEntityTest() throws NimbitsException {

        final List<Entity> entities =  entityService.getEntities(user);
        assertTrue(!entities.isEmpty());
        Entity e = entityService.getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertNotNull(e);
        Entity c = entityService.getEntityByKey(user, pointChild.getKey(), EntityType.point).get(0);
        Entity g = entityService.getEntityByKey(user, group.getKey(), EntityType.point).get(0);

        assertNotNull(c);
        assertNotNull(g);
        final List<Entity> children =  entityService.getChildren(g, EntityType.point);
        assertTrue(!children.isEmpty());

        entityService.deleteEntity(g);
        assertTrue(entityService.getEntityByKey(user, point.getKey(), EntityType.point).isEmpty());
        assertTrue(entityService.getEntityByKey(user, pointChild.getKey(), EntityType.point).isEmpty());
        assertTrue(entityService.getEntityByKey(user, group.getKey(), EntityType.point).isEmpty());



    }

    @Test
    public void findEntityByKeyTest() throws NimbitsException {
        helper.setEnvIsLoggedIn(false);

        user = null;
        req.removeAllParameters();

        List<Entity> r = entityService.findEntityByKey(user, point.getKey());
        assertFalse(r.isEmpty());
//TODO can't seem to logout to test readonly        assertTrue(r.get(0).isReadOnly());
        List<Entity> r2 = entityService.findEntityByKey(user, point.getUUID());
        assertFalse(r2.isEmpty());
  //      assertTrue(r2.get(0).isReadOnly());
        List<Entity> r3 = entityService.findEntityByKey(user, group.getUUID());
        assertFalse(r3.isEmpty());
  //      assertTrue(r3.get(0).isReadOnly());
        List<Entity> r4 = entityService.findEntityByKey(user, group.getKey());
        assertFalse(r4.isEmpty());
  //      assertTrue(r4.get(0).isReadOnly());

    }

    @Test(expected=NimbitsException.class)
    public void duplicateNameTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.point);
        Entity model = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                user.getKey(), user.getKey());

        //  Entity e = entityService.addUpdateEntity(model);
        Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                user.getKey(), user.getKey());
        ///Entity e2 = entityService.addUpdateEntity(model2);
        Point p1 =   PointModelFactory.createPointModel(
                model,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0 );
        entityService.addUpdateEntity(p1);
        Point p2 =   PointModelFactory.createPointModel(
                model2,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic , 0, false, 0.0);
        entityService.addUpdateEntity(p2);

    }
    @Test
    public void duplicateNameGroupsOKTest()  {
        try {
            EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.category);
            Entity model = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());
            Entity e = entityService.addUpdateEntity(model);
            Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());

            Entity e2 = entityService.addUpdateEntity(model2);
        } catch (NimbitsException e1) {
            fail();
        }


    }

    @Test
    public void getEntitiesTest() throws NimbitsException {

        List<Entity> entities =  entityService.getEntities(user);
        assertTrue(!entities.isEmpty());
        int last = 0;
        for (Entity e : entities) {
            assertTrue(e.getEntityType().getOrder() >= last);
            last = e.getEntityType().getOrder(); //proves sorting is working based on type order
        }
    }

    @Test
    public void getEntityByNameTest() throws NimbitsException {
        Point  r = (Point) entityService.getEntityByName(user, pointName, EntityType.point).get(0);
        assertNotNull(r);

    }

    @Test
    public void readOnlyTest() throws NimbitsException {

        List<Entity> r =entityService.getEntities(user);
        for (Entity e : r) {
            assertTrue(e.isReadOnly() != e.getOwner().equals(user.getKey()));
        }

    }

    @Test
    public void testEntitySecurity() throws NimbitsException {
        List<Entity> r =entityService.getEntities(user);
        for (Entity e : r) {

            if (user.getKey().equals(e.getOwner())) {
                assertTrue(e.isOwner(user));
            }
            assertTrue(e.entityIsReadable(user));
        }
    }




}
