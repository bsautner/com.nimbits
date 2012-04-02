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
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.point.PointTransactionsFactory;
import helper.NimbitsServletTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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
        Entity result = EntityTransactionFactory.getDaoInstance(user).addUpdateEntity(entity);
        Entity r = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(result.getKey());
        assertNotNull(r);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(r.getKey());

        Point point = PointModelFactory.createPointModel();
        Point px = PointTransactionsFactory.getDaoInstance(user).addPoint(result, point);

        Point rp = PointTransactionsFactory.getDaoInstance(user).getPointByKey(result.getKey());
        assertNotNull(px);
       assertNotNull(rp);
    }

}
