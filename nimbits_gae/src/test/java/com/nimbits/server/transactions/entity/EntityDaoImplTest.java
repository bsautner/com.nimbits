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
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EntityDaoImplTest extends NimbitsServletTest {

    EntityDao dao;

    @Before
    public void setup() {
        super.setup();


    }

    @Test
    public void addUpdateEntityTest() throws Exception {

        EntityName name = CommonFactory.createName("e534", EntityType.point);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey());
        // Point result = PointServiceFactory.getInstance().addPoint(user, entity);
        Point add = PointModelFactory.createPointModel(
                entity,
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
                PointType.basic, 0, false, 0.0, 10);
        Entity result = entityService.addUpdateEntity(user, add).get(0);
        //Entity result = EntityDao.addUpdateSingleEntity(entity);
        Entity r = dao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point1 = PointModelFactory.createPointModel(
                r,
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
                PointType.basic, 0, false, 0.0, 10);
        Point px = (Point) entityService.addUpdateSingleEntity(user, point1);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        Point rp = (Point) dao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);

        assertNotNull(px);
        assertNotNull(rp);


    }


    @Test
    public void addUpdateAlertsEntityTest3() throws Exception {

        EntityName name = CommonFactory.createName("e534", EntityType.point);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey());
        // Point result = PointServiceFactory.getInstance().addPoint(user, entity);
        Point add = PointModelFactory.createPointModel(
                entity,
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
                PointType.basic, 0, false, 0.0, 10);
        Entity result = entityService.addUpdateEntity(user, add).get(0);
        //Entity result = EntityDao.addUpdateSingleEntity(entity);
        Entity r = dao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point1 = PointModelFactory.createPointModel(
                r,
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
                PointType.basic, 0, false, 0.0, 10);
        Point px = (Point) entityService.addUpdateSingleEntity(user, point1);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        Point rp = (Point) dao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);

        rp.setHighAlarmOn(true);
        rp.setLowAlarmOn(true);
        rp.setIdleAlarmOn(true);
        dao.addUpdateEntity(user, Arrays.<Entity>asList(rp));
        Point xp = (Point) dao.getEntityByKey(user, rp.getKey(), PointEntity.class).get(0);
        assertTrue(xp.isHighAlarmOn());
        assertTrue(xp.isLowAlarmOn());
        assertTrue(xp.isIdleAlarmOn());


        assertNotNull(px);
        assertNotNull(rp);


    }

    @Test
    public void deleteEntityTest() throws Exception {

        final EntityName name = CommonFactory.createName("point to delete", EntityType.point);

        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey());

        Point point1 = PointModelFactory.createPointModel(
                entity,
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
                PointType.basic, 0, false, 0.0, 10);

        final Entity result = entityService.addUpdateEntity(user, Arrays.<Entity>asList(point1)).get(0);
        //Entity result = EntityDao.addUpdateSingleEntity(entity);
        final Entity r = dao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        // final Point point = PointModelFactory.createPointModel(entity);
        //final Point px = PointTransactionsFactory.getDaoInstance(user).addPoint(point);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        final Point rp = (Point) dao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);

        // assertNotNull(px);
        assertNotNull(rp);
        dao.deleteEntity(user, r, PointEntity.class);
        assertTrue(dao.getEntityByKey(user, result.getKey(), PointEntity.class).isEmpty());

    }

    @Test
    public void readOnlyTest() throws Exception {

        List<Entity> r = dao.getEntities(user);
        for (Entity e : r) {
            assertTrue(e.isReadOnly() != e.getOwner().equals(user.getKey()));
        }

    }

    @Test
    public void testEntitySecurity() throws Exception {
        List<Entity> r = dao.getEntities(user);
        for (Entity e : r) {
            if (user.getKey().equals(e.getOwner())) {
                assertTrue(e.isOwner(user));
            }
            assertTrue(e.entityIsReadable(user));
        }

    }

    @Test
    public void testEntityTree() {
        List<Entity> r = dao.getEntities(user);
        assertFalse(r.isEmpty());

    }

    @Test(expected = Exception.class)
    public void testValidation() throws Exception {

        final EntityName name = CommonFactory.createName("should fail#$%#$%%#%#$^#$", EntityType.point);


        Entity e = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.onlyMe, user.getKey(), user.getKey());
        dao.addUpdateEntity(user, Arrays.<Entity>asList(e));


    }
}
