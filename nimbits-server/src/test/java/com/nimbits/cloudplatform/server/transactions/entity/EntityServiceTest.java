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

package com.nimbits.cloudplatform.server.transactions.entity;


import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.helper.EntityHelper;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
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
    private final EntityService service = EntityServiceFactory.getInstance();

    @Test
    public void addUpdateEntity() throws Exception {
        Point p = new EntityHelper().createPointWithName(UUID.randomUUID().toString());
        Point r = (Point) service.addUpdateSingleEntity(p);
        assertNotNull(p);
        assertNotNull(r);

    }


    @Test
    public void deleteEntityTest() throws Exception {

        final List<Entity> entities =  service.getEntities(user);
        assertTrue(!entities.isEmpty());
        Entity e = service.getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertNotNull(e);
        Entity c = service.getEntityByKey(user, pointChild.getKey(), EntityType.point).get(0);
        List<Entity> g = service.getEntityByKey(user, group.getKey(), EntityType.point);

        assertNotNull(c);
        assertNotNull(g);
        final List<Entity> children =  service.getChildren(user, g);
        assertTrue(!children.isEmpty());

        service.deleteEntity(user, g);
        assertTrue(service.getEntityByKey(user, point.getKey(), EntityType.point).isEmpty());
        assertTrue(service.getEntityByKey(user, pointChild.getKey(), EntityType.point).isEmpty());
        assertTrue(service.getEntityByKey(user, group.getKey(), EntityType.point).isEmpty());



    }

    @Test
    public void findEntityByKeyTest() throws Exception {
        helper.setEnvIsLoggedIn(false);

        //user = null;
        req.removeAllParameters();

        List<Entity> r = service.findEntityByKey(user, point.getKey());
        assertFalse(r.isEmpty());
//TODO can't seem to logout to test readonly        assertTrue(r.get(0).isReadOnly());
        List<Entity> r2 = service.findEntityByKey(user, point.getUUID());
        assertFalse(r2.isEmpty());
  //      assertTrue(r2.get(0).isReadOnly());
        List<Entity> r3 = service.findEntityByKey(user, group.getUUID());
        assertFalse(r3.isEmpty());
  //      assertTrue(r3.get(0).isReadOnly());
        List<Entity> r4 = service.findEntityByKey(user, group.getKey());
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
        service.addUpdateSingleEntity(p1);
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
        service.addUpdateSingleEntity(p2);

    }
    @Test
    public void duplicateNameGroupsOKTest()  {
        try {
            EntityName name = CommonFactory.createName("TWICE", EntityType.category);
            Entity model = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());
            Entity e = service.addUpdateSingleEntity(model);
            Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());

            Entity e2 = service.addUpdateSingleEntity(model2);
        } catch (Exception e1) {
            fail();
        }


    }

    @Test
    public void getEntitiesTest() throws Exception {

        List<Entity> entities =  service.getEntities(user);
        assertTrue(!entities.isEmpty());
        int last = 0;
        for (Entity e : entities) {
            assertTrue(e.getEntityType().getOrder() >= last);
            last = e.getEntityType().getOrder(); //proves sorting is working based on type order
        }
    }

    @Test
    public void getEntityByNameTest() throws Exception {
        Point  r = (Point) service.getEntityByName(user, pointName, EntityType.point).get(0);
        assertNotNull(r);

    }

    @Test
    public void readOnlyTest() throws Exception {

        List<Entity> r = service.getEntities(user);
        for (Entity e : r) {
            assertTrue(e.isReadOnly() != e.getOwner().equals(user.getKey()));
        }

    }

    @Test
    public void testEntitySecurity() throws Exception {
        List<Entity> r = service.getEntities(user);
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
        Map<String, Entity> map = service.getEntityModelMap(user, EntityType.point, 1000);
        List<Entity> sample = new ArrayList<Entity>(1);
        sample.add(point);
        List<Entity> result =  service.getChildren(user, sample);
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
        return   service.addUpdateEntity(user, Arrays.<Entity>asList(newChild)).get(0);

    }



}
