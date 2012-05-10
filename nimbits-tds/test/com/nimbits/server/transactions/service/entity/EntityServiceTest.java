/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.helper.EntityHelper;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.*;
import static org.junit.Assert.*;

import com.nimbits.server.transactions.service.point.PointServiceFactory;
import org.junit.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 2:27 PM
 */
@SuppressWarnings("FeatureEnvy")
public class EntityServiceTest extends NimbitsServletTest {


    @Test
    public void addUpdateEntity() throws NimbitsException {
        Point p = EntityHelper.createPointWithName(UUID.randomUUID().toString());
        Point r = (Point) EntityServiceFactory.getInstance().addUpdateEntity(p);
        assertNotNull(p);
        assertNotNull(r);

    }


    @Test
    public void deleteEntityTest() throws NimbitsException {

        final List<Entity> entities =  EntityServiceFactory.getInstance().getEntities();
        assertTrue(!entities.isEmpty());
        Entity e = EntityServiceFactory.getInstance().getEntityByKey(user, point.getKey(), EntityType.point).get(0);
        assertNotNull(e);
        Entity c = EntityServiceFactory.getInstance().getEntityByKey(user, pointChild.getKey(), EntityType.point).get(0);
        Entity g = EntityServiceFactory.getInstance().getEntityByKey(user, group.getKey(), EntityType.point).get(0);

        assertNotNull(c);
        assertNotNull(g);
        final List<Entity> children =  EntityServiceFactory.getInstance().getChildren(g, EntityType.point);
        assertTrue(!children.isEmpty());

        EntityServiceFactory.getInstance().deleteEntity(g);
        assertTrue(EntityServiceFactory.getInstance().getEntityByKey(user, point.getKey(), EntityType.point).isEmpty());
        assertTrue(EntityServiceFactory.getInstance().getEntityByKey(user, pointChild.getKey(), EntityType.point).isEmpty());
        assertTrue(EntityServiceFactory.getInstance().getEntityByKey(user, group.getKey(), EntityType.point).isEmpty());



    }

    @Test
    public void findEntityByKeyTest() throws NimbitsException {
        helper.setEnvIsLoggedIn(false);
        List<Entity> r = EntityServiceFactory.getInstance().findEntityByKey(point.getKey());
        assertFalse(r.isEmpty());
        assertTrue(r.get(0).isReadOnly());
        List<Entity> r2 = EntityServiceFactory.getInstance().findEntityByKey(point.getUUID());
        assertFalse(r2.isEmpty());
        assertTrue(r2.get(0).isReadOnly());
        List<Entity> r3 = EntityServiceFactory.getInstance().findEntityByKey(group.getUUID());
        assertFalse(r3.isEmpty());
        assertTrue(r3.get(0).isReadOnly());
        List<Entity> r4 = EntityServiceFactory.getInstance().findEntityByKey(group.getKey());
        assertFalse(r4.isEmpty());
        assertTrue(r4.get(0).isReadOnly());

    }

    @Test(expected=NimbitsException.class)
    public void duplicateNameTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.point);
        Entity model = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                user.getKey(), user.getKey());

        //  Entity e = EntityServiceFactory.getInstance().addUpdateEntity(model);
        Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                user.getKey(), user.getKey());
        ///Entity e2 = EntityServiceFactory.getInstance().addUpdateEntity(model2);
        Point p1 = PointModelFactory.createPointModel( model);
        EntityServiceFactory.getInstance().addUpdateEntity(p1);
        Point p2 = PointModelFactory.createPointModel( model2);
        EntityServiceFactory.getInstance().addUpdateEntity(p2);

    }
    @Test
    public void duplicateNameGroupsOKTest()  {
        try {
            EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.category);
            Entity model = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());
            Entity e = EntityServiceFactory.getInstance().addUpdateEntity(model);
            Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    user.getKey(), user.getKey());

            Entity e2 = EntityServiceFactory.getInstance().addUpdateEntity(model2);
        } catch (NimbitsException e1) {
            fail();
        }


    }

    @Test
    public void getEntitiesTest() throws NimbitsException {

        List<Entity> entities =  EntityServiceFactory.getInstance().getEntities();
        assertTrue(!entities.isEmpty());
        int last = 0;
        for (Entity e : entities) {
            assertTrue(e.getEntityType().getOrder() >= last);
            last = e.getEntityType().getOrder(); //proves sorting is working based on type order
        }
    }

    @Test
    public void getEntityByNameTest() throws NimbitsException {
        Point  r = (Point) EntityServiceFactory.getInstance().getEntityByName(user, pointName, EntityType.point).get(0);
        assertNotNull(r);

    }

    @Test
    public void readOnlyTest() throws NimbitsException {

        List<Entity> r =EntityServiceFactory.getInstance().getEntities();
        for (Entity e : r) {
            assertTrue(e.isReadOnly() != e.getOwner().equals(user.getKey()));
        }

    }

    @Test
    public void testEntitySecurity() throws NimbitsException {
        List<Entity> r =EntityServiceFactory.getInstance().getEntities();
        for (Entity e : r) {

            if (user.getKey().equals(e.getOwner())) {
                assertTrue(e.isOwner(user));
            }
            assertTrue(e.entityIsReadable(user));
        }
    }




}
