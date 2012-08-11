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

package com.nimbits.server.transactions.memcache.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.CommonFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.orm.CalcEntity;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/8/12
 * Time: 10:21 AM
 */
public class EntityCacheImplTest extends NimbitsServletTest {







    @Test
    public void testRemoveEntityFromCache() throws Exception {
        entityTransactions.addEntityToCache(Arrays.asList((Entity)point));
        List<Entity> e = entityTransactions.getEntityFromCache(point.getKey());
        assertFalse(e.isEmpty());
        entityTransactions.removeEntityFromCache(Arrays.asList((Entity)point));
        List<Entity> e2 = entityTransactions.getEntityFromCache(point.getKey());
        assertTrue(e2.isEmpty());
    }

    @Test
    public void testGetEntityByName() throws Exception {

    }

    @Test
    public void testGetEntitiesBySource() throws Exception {

    }

    @Test
    public void testGetEntityByTrigger() throws Exception {
        EntityName name = CommonFactoryLocator.getInstance().createName("calc", EntityType.calculation);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, point.getKey(), user.getKey());

        Calculation c = CalculationModelFactory.createCalculation(entity, point.getKey(), true, "1+1", pointChild.getKey(), "", "", "");
        entityTransactions.addUpdateEntity(c);
        List<Entity> triggers = entityTransactions.getEntityByTrigger(point, CalcEntity.class);
        assertFalse(triggers.isEmpty());

        for (Entity e : triggers) {
            assertFalse(e.isCached());
        }

        List<Entity> triggers2 = entityTransactions.getEntityByTrigger(point, CalcEntity.class);
        assertFalse(triggers2.isEmpty());

        for (Entity e : triggers2) {
            assertTrue(e.isCached());
        }


        EntityName name2 = CommonFactoryLocator.getInstance().createName("calc2", EntityType.calculation);

        Entity entity2 = EntityModelFactory.createEntity(name2, "", EntityType.calculation, ProtectionLevel.onlyMe, point.getKey(), user.getKey());

        Calculation c2 = CalculationModelFactory.createCalculation(entity2, point.getKey(), true, "1+2", pointChild.getKey(), "", "", "");
        entityTransactions.addUpdateEntity(c2);

        List<Entity> triggers3 = entityTransactions.getEntityByTrigger(point, CalcEntity.class);
        assertFalse(triggers3.isEmpty());

        for (Entity e : triggers3) {
            assertFalse(e.isCached());
        }

        List<Entity> triggers4 = entityTransactions.getEntityByTrigger(point, CalcEntity.class);
        assertFalse(triggers4.isEmpty());

        for (Entity e : triggers4) {
            assertTrue(e.isCached());
        }


    }

    @Test
    public void testGetIdleEntities() throws Exception {

    }

    @Test
    public void testGetSubscriptionsToEntity() throws Exception {

    }

    @Test
    public void testGetEntityByBlobKey() throws Exception {

    }

    @Test
    public void testGetEntityMap() throws Exception {

    }

    @Test
    public void testGetEntityNameMap() throws Exception {

    }

    @Test
    public void testGetChildren() throws Exception {

    }

    @Test
    public void testAddUpdateEntity() throws Exception {

    }

    @Test
    public void testGetEntities() throws Exception {
        List<Entity> results = entityTransactions.getEntities();
        assertFalse(results.isEmpty());
        for (Entity e: results) {
            assertFalse(e.isCached());

        }

        List<Entity> results2 = entityTransactions.getEntities();
        assertFalse(results2.isEmpty());
        for (Entity e: results2) {
            assertTrue(e.isCached());

        }
        point.setExpire(50);
        entityTransactions.addUpdateEntity(point);

        List<Entity> results3 = entityTransactions.getEntities();
        assertFalse(results3.isEmpty());
        for (Entity e: results3) {
            assertFalse(e.isCached());

        }

        List<Entity> results4 = entityTransactions.getEntities();
        assertFalse(results4.isEmpty());
        for (Entity e: results4) {
            assertTrue(e.isCached());

        }
    }

    @Test
    public void testDeleteEntity() throws Exception {

    }

    @Test
    public void testGetEntityByKey() throws Exception {

    }

    @Test
    public void testGetSystemWideEntityMap() throws Exception {

    }
}
