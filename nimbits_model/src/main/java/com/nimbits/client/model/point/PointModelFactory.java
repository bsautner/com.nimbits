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

package com.nimbits.client.model.point;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;

import java.util.UUID;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:06 PM
 */
public class PointModelFactory {


    private PointModelFactory() {
    }


    public static Point createPointModel(final Point point)  {

        return new PointModel(point);

    }
    public static Point createPointModel(final Entity entity,
                                         final double highAlarm,
                                         final int expire,
                                         final String unit,
                                         final double lowAlarm,
                                         final boolean highAlarmOn,
                                         final boolean lowAlarmOn,
                                         final boolean idleAlarmOn,
                                         final Integer idleSeconds,
                                         final boolean idleAlarmSent,
                                         final FilterType filterType,
                                         final double filterValue,
                                         final boolean inferLocation,
                                         final PointType pointType,
                                         final Integer deltaAlarmSeconds,
                                         final boolean deltaAlarmOn,
                                         final double deltaAlarm
                                        )   {
        return new PointModel(
                entity,  highAlarm,
                expire,  unit,  lowAlarm,
                highAlarmOn,  lowAlarmOn,
                idleAlarmOn,  idleSeconds,  idleAlarmSent,
                filterType,  filterValue, inferLocation, pointType,deltaAlarm, deltaAlarmOn, deltaAlarmSeconds);
    }


    public static Point createPointModel(User user, String name) {

        EntityName pointName;
        pointName = CommonFactory.createName(name, EntityType.point);
        Entity pointEntity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone, user.getKey(), user.getKey(), "");
        Point newPoint = PointModelFactory.createPointModel(
                pointEntity,
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
                PointType.basic, 0, false, 0.0);
        newPoint.setExpire(5);

        return newPoint;


    }
}
