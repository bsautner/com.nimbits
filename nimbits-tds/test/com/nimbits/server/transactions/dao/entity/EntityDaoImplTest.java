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
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.orm.PointEntity;
import helper.NimbitsServletTest;
import org.junit.Test;

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

        EntityName name = CommonFactoryLocator.getInstance().createName("e", EntityType.point);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, "", "");
       // Point result = PointServiceFactory.getInstance().addPoint(user, entity);
        Point add = PointModelFactory.createPointModel(entity);
        Point result = (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, add);
        //Entity result = EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        Entity r = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point = PointModelFactory.createPointModel(entity);
        Point px = (Point) EntityServiceFactory.getInstance().addUpdateEntity(point);

        //Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        Point rp = (Point) EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey(), PointEntity.class).get(0);

        assertNotNull(px);
       assertNotNull(rp);



    }
    @Test
    public void deleteEntityTest() throws NimbitsException {

        final EntityName name = CommonFactoryLocator.getInstance().createName("point to delete", EntityType.point);

        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone, "", "");
        final Point result = (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
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
}
