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
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.transactions.service.entity.EntityTransactions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 3:21 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class EntityDaoImplTest extends NimbitsServletTest {
    
    @Resource(name="entityDao")
    EntityTransactions entityDao;
    
    
    @Test
    public void addUpdateEntityTest() throws NimbitsException {

        EntityName name = CommonFactory.createName("e534", EntityType.point);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey());
        // Point result = PointServiceFactory.getInstance().addPoint(user, entity);
        Point add =   PointModelFactory.createPointModel(
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
                PointType.basic, 0, false, 0.0 );
        Entity result = entityService.addUpdateEntity(user, add);
        //Entity result = entityDao.addUpdateEntity(entity);
        Entity r = entityDao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point1=   PointModelFactory.createPointModel(
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
                PointType.basic, 0, false, 0.0 );
        Point px = (Point) entityService.addUpdateEntity(point1);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        Point rp = (Point) entityDao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);

        assertNotNull(px);
        assertNotNull(rp);



    }


    @Test
    public void addUpdateAlertsEntityTest3() throws NimbitsException {

        EntityName name = CommonFactory.createName("e534", EntityType.point);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey());
        // Point result = PointServiceFactory.getInstance().addPoint(user, entity);
        Point add =   PointModelFactory.createPointModel(
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
                PointType.basic, 0, false, 0.0 );
        Entity result = entityService.addUpdateEntity(user, add);
        //Entity result = entityDao.addUpdateEntity(entity);
        Entity r = entityDao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point1=   PointModelFactory.createPointModel(
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
                PointType.basic , 0, false, 0.0);
        Point px = (Point) entityService.addUpdateEntity(point1);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        Point rp = (Point) entityDao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);

        rp.setHighAlarmOn(true);
        rp.setLowAlarmOn(true);
        rp.setIdleAlarmOn(true);
        entityDao.addUpdateEntity(user, rp);
        Point xp = (Point) entityDao.getEntityByKey(user, rp.getKey(), PointEntity.class).get(0);
        assertTrue(xp.isHighAlarmOn());
        assertTrue(xp.isLowAlarmOn());
        assertTrue(xp.isIdleAlarmOn());


        assertNotNull(px);
        assertNotNull(rp);



    }

    @Test
    public void deleteEntityTest() throws NimbitsException {

        final EntityName name = CommonFactory.createName("point to delete", EntityType.point);

        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, user.getKey(),user.getKey());

        Point point1=   PointModelFactory.createPointModel(
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
                PointType.basic , 0, false, 0.0);

        final Entity result = entityService.addUpdateEntity(user, point1);
        //Entity result = entityDao.addUpdateEntity(entity);
        final Entity r = entityDao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        // final Point point = PointModelFactory.createPointModel(entity);
        //final Point px = PointTransactionsFactory.getDaoInstance(user).addPoint(point);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        final Point rp = (Point) entityDao.getEntityByKey(user, result.getKey(), PointEntity.class).get(0);

        // assertNotNull(px);
        assertNotNull(rp);
        entityDao.deleteEntity(user, r, PointEntity.class);
        assertTrue(entityDao.getEntityByKey(user, result.getKey(),PointEntity.class).isEmpty());

    }

    @Test
    public void readOnlyTest() throws NimbitsException {

        List<Entity> r = entityDao.getEntities(user);
        for (Entity e : r) {
             assertTrue(e.isReadOnly() != e.getOwner().equals(user.getKey()));
        }

    }

    @Test
    public void testEntitySecurity() throws NimbitsException {
        List<Entity> r = entityDao.getEntities(user);
        for (Entity e : r) {
            if (user.getKey().equals(e.getOwner())) {
                assertTrue(e.isOwner(user));
            }
            assertTrue(e.entityIsReadable(user));
        }

    }


    @Test(expected = NimbitsException.class)
    public void testValidation() throws NimbitsException {

        final EntityName name = CommonFactory.createName("should fail#$%#$%%#%#$^#$", EntityType.point);


        Entity e = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.onlyMe,user.getKey(), user.getKey());
        entityDao.addUpdateEntity(user, e, true);


    }
}
