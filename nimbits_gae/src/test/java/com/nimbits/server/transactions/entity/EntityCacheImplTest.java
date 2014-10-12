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

package com.nimbits.server.transactions.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.orm.CalcEntity;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.cache.EntityCache;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;


public class EntityCacheImplTest extends NimbitsServletTest {

    EntityCache cache;

    @Before
    public void setup() {
        super.setup();
        cache = EntityServiceFactory.getCacheInstance(engine);

    }

    @Test
    public void testGetEntityByName() throws Exception {

    }

    @Test
    public void testGetEntitiesBySource() throws Exception {

    }

    @Test
    public void testGetEntityByTrigger() throws Exception {
        EntityName name = CommonFactory.createName("calc", EntityType.calculation);

        Entity entity = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe, point.getKey(), user.getKey());

        Calculation c = CalculationModelFactory.createCalculation(entity, EntityModelFactory.createTrigger(point.getKey()), true, "1+1", EntityModelFactory.createTarget(pointChild.getKey()), "", "", "");
        entityService.addUpdateEntity(user, c);
        List<Entity> triggers = cache.getEntityByTrigger(user, point, CalcEntity.class);
        assertFalse(triggers.isEmpty());

        for (Entity e : triggers) {
            assertFalse(e.isCached());
        }

        List<Entity> triggers2 = entityService.getEntityByTrigger(user, point, EntityType.calculation);
        assertFalse(triggers2.isEmpty());

        for (Entity e : triggers2) {
//TODO Cache entities            assertTrue(e.isCached());
        }


        EntityName name2 = CommonFactory.createName("calc2", EntityType.calculation);

        Entity entity2 = EntityModelFactory.createEntity(name2, "", EntityType.calculation, ProtectionLevel.onlyMe, point.getKey(), user.getKey());

        Calculation c2 = CalculationModelFactory.createCalculation(entity2, EntityModelFactory.createTrigger(point.getKey()), true, "1+2", EntityModelFactory.createTarget(pointChild.getKey()), "", "", "");
        entityService.addUpdateEntity(user, c2);

        List<Entity> triggers3 = cache.getEntityByTrigger(user, point, CalcEntity.class);
        assertFalse(triggers3.isEmpty());

        for (Entity e : triggers3) {
            assertFalse(e.isCached());
        }

        List<Entity> triggers4 = cache.getEntityByTrigger(user, point, CalcEntity.class);
        assertFalse(triggers4.isEmpty());

        for (Entity e : triggers4) {
//TODO            assertTrue(e.isCached());
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
    public void testDeleteEntity() throws Exception {

    }

    @Test
    public void testGetEntityByKey() throws Exception {

    }

    @Test
    public void testGetSystemWideEntityMap() throws Exception {

    }
}
