/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.admin.system;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/27/12
 * Time: 4:32 PM
 */
@Service("systemService")

public class SystemServiceImpl {


    private static final int EXPIRE = 365;

    public static void updateSystemPoint(
            final String pointName,
            final double value,
            final boolean incrementAsCounter,
            final PointType type) throws Exception {

        EntityName name = CommonFactory.createName(pointName, EntityType.point);
        User admin =UserTransaction.getAdmin();
        List<Entity> e = EntityServiceImpl.getEntityByName(admin,
                name, EntityType.point);
        Point p;
        if (e.isEmpty()) {
            String ownerKey = admin.getKey();
            Entity ep = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.onlyMe, ownerKey,
                    ownerKey, UUID.randomUUID().toString());
            Point pm = PointModelFactory.createPointModel(ep, 0.0, EXPIRE, "", 0.0, false, false, false, 0,
                    false, FilterType.none, 0.0, false, type, 0, false, 0.0);
            p = (Point)EntityServiceImpl.addUpdateEntity(admin, Arrays.<Entity>asList(pm));
        }
        else {
            p = (Point) e.get(0);

        }
        Value vx;
        if (incrementAsCounter) {
           List<Value> c = ValueTransaction.getCurrentValue(p);
           double cd = ! c.isEmpty() ? c.get(0).getDoubleValue() : 0.0;
           vx = ValueFactory.createValueModel(cd + value);
        }
        else {
            vx = ValueFactory.createValueModel(value);
        }
        ValueTransaction.recordValue(admin, p, vx);

    }


}
