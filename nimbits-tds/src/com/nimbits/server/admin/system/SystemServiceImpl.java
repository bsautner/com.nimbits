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

package com.nimbits.server.admin.system;

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
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.transactions.service.entity.EntityServiceImpl;
import com.nimbits.server.transactions.service.user.UserServiceImpl;
import com.nimbits.server.transactions.service.value.ValueServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/27/12
 * Time: 4:32 PM
 */
@Service("systemService")
@Transactional
public class SystemServiceImpl implements SystemService{


    private static final int EXPIRE = 365;
    private EntityServiceImpl entityService;
    private UserServiceImpl userService;
    private ValueServiceImpl valueService;

    @Override
    public void updateSystemPoint(
            final String pointName,
            final double value,
            final boolean incrementAsCounter,
            final PointType type) throws NimbitsException {

        EntityName name = CommonFactory.createName(pointName, EntityType.point);
        User admin =userService.getAdmin();
        List<Entity> e = entityService.getEntityByName(admin,
                name, EntityType.point);
        Point p;
        if (e.isEmpty()) {
            String ownerKey = admin.getKey();
            Entity ep = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.onlyMe, ownerKey,
                    ownerKey, UUID.randomUUID().toString());
            Point pm = PointModelFactory.createPointModel(ep, 0.0, EXPIRE, "", 0.0, false, false, false, 0,
                    false, FilterType.none, 0.0, false, type, 0, false, 0.0);
            p = (Point)entityService.addUpdateEntity(admin, pm);
        }
        else {
            p = (Point) e.get(0);

        }
        Value vx;
        if (incrementAsCounter) {
           List<Value> c = valueService.getCurrentValue(p);
           double cd = ! c.isEmpty() ? c.get(0).getDoubleValue() : 0.0;
           vx = ValueFactory.createValueModel(cd + value);
        }
        else {
            vx = ValueFactory.createValueModel(value);
        }
        valueService.recordValue(admin, p, vx);

    }

    public void setEntityService(EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    public EntityServiceImpl getEntityService() {
        return entityService;
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public UserServiceImpl getUserService() {
        return userService;
    }

    public void setValueService(ValueServiceImpl valueService) {
        this.valueService = valueService;
    }

    public ValueServiceImpl getValueService() {
        return valueService;
    }
}
