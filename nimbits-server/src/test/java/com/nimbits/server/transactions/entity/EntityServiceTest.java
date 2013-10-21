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

package com.nimbits.server.transactions.entity;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.helper.EntityHelper;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 2:27 PM
 */

public class EntityServiceTest extends NimbitsServletTest {
 

    @Test
    public void addUpdateEntity() throws Exception {
        Point p = new EntityHelper().createPointWithName(UUID.randomUUID().toString());
        Point r = (Point) entityService.addUpdateSingleEntity(p);
        assertNotNull(p);
        assertNotNull(r);

    }


    @Test
    public void deleteEntityTest() throws Exception {

        final List<Entity> entities =  entityService.getEntities(user);
        assertTrue(!entities.isEmpty());
        Entity e = entityService.getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertNotNull(e);
        Entity c = entityService.getEntityByKey(user, pointChild.getKey(), EntityType.point).get(0);
        List<Entity> g = entityService.getEntityByKey(user, group.getKey(), EntityType.category);

        assertNotNull(c);
        assertNotNull(g);
        final List<Entity> children =  entityService.getChildren(user, g);
        assertTrue(!children.isEmpty());

        entityService.deleteEntity(user, g);
        assertTrue(entityService.getEntityByKey(user, point.getKey(), EntityType.point).isEmpty());
        assertTrue(entityService.getEntityByKey(user, pointChild.getKey(), EntityType.point).isEmpty());
        assertTrue(entityService.getEntityByKey(user, group.getKey(), EntityType.point).isEmpty());



    }

    @Test
    public void findEntityByKeyTest() throws Exception {
        helper.setEnvIsLoggedIn(false);

        //user = null;
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

    @Test(expected=Exception.class)
    public void duplicateNameTest() throws Exception {

        EntityName name = CommonFactory.createName("TWICE", EntityType.point);
        Entity model = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                user.getKey(), user.getKey());

        //  Entity e = serviceImpl.addUpdateSingleEntity(model);
        Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                user.getKey(), user.getKey());
        ///Entity e2 = serviceImpl.addUpdateSingleEntity(model2);
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
                PointType.basic, 0, false, 0.0);
        entityService.addUpdateSingleEntity(p1);
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
        entityService.addUpdateSingleEntity(p2);

    }
    @Test
    public void duplicateNameGroupsOKTest()  {
        try {
            EntityName name = CommonFactory.createName("TWICE", EntityType.category);
            Entity model = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());
            Entity e = entityService.addUpdateSingleEntity(model);
            Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());

            Entity e2 = entityService.addUpdateSingleEntity(model2);
        } catch (Exception e1) {
            fail();
        }


    }

    @Test
    public void getEntitiesTest() throws Exception {

        List<Entity> entities =  entityService.getEntities(user);
        assertTrue(!entities.isEmpty());
        int last = 0;
        for (Entity e : entities) {
            assertTrue(e.getEntityType().getOrder() >= last);
            last = e.getEntityType().getOrder(); //proves sorting is working based on type order
        }
    }

    @Test
    public void getEntityByNameTest() throws Exception {
        Point  r = (Point) entityService.getEntityByName(user, pointName, EntityType.point).get(0);
        assertNotNull(r);

    }

    @Test
    public void readOnlyTest() throws Exception {

        List<Entity> r = entityService.getEntities(user);
        for (Entity e : r) {
            assertTrue(e.isReadOnly() != e.getOwner().equals(user.getKey()));
        }

    }

    @Test
    public void testEntitySecurity() throws Exception {
        List<Entity> r = entityService.getEntities(user);
        for (Entity e : r) {

            if (user.getKey().equals(e.getOwner())) {
                assertTrue(e.isOwner(user));
            }
            assertTrue(e.entityIsReadable(user));
        }
    }


    @Test
    public void testGetEntityChildren() throws Exception {

        Entity e = point;
        String[] a = {"a", "b", "c", "d", "e", "f", "g"};
        for (String anA : a) {

            e = createChild(e, anA);

        }
        Map<String, Entity> map = entityService.getEntityModelMap(user, EntityType.point, 1000);
        List<Entity> sample = new ArrayList<Entity>(1);
        sample.add(point);
        List<Entity> result =  entityService.getChildren(user, sample);
        assertNotNull(result);
        assertTrue(! result.isEmpty());



        assertEquals(8, result.size());


    }



    private Entity createChild(Entity parent, String name) throws Exception {
        EntityName n = CommonFactory.createName(name, EntityType.point);
        Entity pointChildEntity = EntityModelFactory.createEntity(n, "", EntityType.point, ProtectionLevel.everyone, parent.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newChild =  PointModelFactory.createPointModel(
                pointChildEntity,
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
        return   entityService.addUpdateEntity(user, Arrays.<Entity>asList(newChild)).get(0);

    }



}
