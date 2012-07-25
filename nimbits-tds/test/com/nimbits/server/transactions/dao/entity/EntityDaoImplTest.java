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

package com.nimbits.server.transactions.dao.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.entity.EntityTransactionFactory;
import com.nimbits.server.orm.PointEntity;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 3:21 PM
 */
public class EntityDaoImplTest extends NimbitsServletTest {

    @Test
    public void addUpdateEntityTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("e534", EntityType.point);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey());
        // Point result = PointServiceFactory.getInstance().addPoint(user, entity);
        Point add = PointModelFactory.createPointModel(entity);
        Entity result = EntityServiceFactory.getInstance().addUpdateEntity(user, add);
        //Entity result = EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        Entity r = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point1 = PointModelFactory.createPointModel(r);
        Point px = (Point) EntityServiceFactory.getInstance().addUpdateEntity(point1);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        Point rp = (Point) EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);

        assertNotNull(px);
        assertNotNull(rp);



    }


    @Test
    public void addUpdateAlertsEntityTest3() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("e534", EntityType.point);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey());
        // Point result = PointServiceFactory.getInstance().addPoint(user, entity);
        Point add = PointModelFactory.createPointModel(entity);
        Entity result = EntityServiceFactory.getInstance().addUpdateEntity(user, add);
        //Entity result = EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        Entity r = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point1 = PointModelFactory.createPointModel(r);
        Point px = (Point) EntityServiceFactory.getInstance().addUpdateEntity(point1);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        Point rp = (Point) EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);

        rp.setHighAlarmOn(true);
        rp.setLowAlarmOn(true);
        rp.setIdleAlarmOn(true);
        EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(rp);
        Point xp = (Point) EntityTransactionFactory.getDaoInstance(user).getEntityByKey(rp.getKey(), PointEntity.class).get(0);
        assertTrue(xp.isHighAlarmOn());
        assertTrue(xp.isLowAlarmOn());
        assertTrue(xp.isIdleAlarmOn());



        assertNotNull(px);
        assertNotNull(rp);



    }

    @Test
    public void deleteEntityTest() throws NimbitsException {

        final EntityName name = CommonFactoryLocator.getInstance().createName("point to delete", EntityType.point);

        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(),user.getKey());
        final Entity result = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
        //Entity result = EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        final Entity r = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        // final Point point = PointModelFactory.createPointModel(entity);
        //final Point px = PointTransactionsFactory.getDaoInstance(user).addPoint(point);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        final Point rp = (Point) EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);

        // assertNotNull(px);
        assertNotNull(rp);
        EntityTransactionFactory.getDaoInstance(user).deleteEntity(r, PointEntity.class);
        assertTrue(EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(),PointEntity.class).isEmpty());

    }

    @Test
    public void readOnlyTest() throws NimbitsException {

        List<Entity> r = EntityTransactionFactory.getInstance(user).getEntities();
        for (Entity e : r) {
             assertTrue(e.isReadOnly() != e.getOwner().equals(user.getKey()));
        }

    }

    @Test
    public void testEntitySecurity() throws NimbitsException {
        List<Entity> r = EntityTransactionFactory.getInstance(user).getEntities();
        for (Entity e : r) {
            if (user.getKey().equals(e.getOwner())) {
                assertTrue(e.isOwner(user));
            }
            assertTrue(e.entityIsReadable(user));
        }

    }

    @Test
    public void testValidation() throws NimbitsException {

        final EntityName name = CommonFactoryLocator.getInstance().createName("should fail", EntityType.point);


        Entity e = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.onlyMe,user.getKey(), user.getKey());
        EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(e);


    }
}
