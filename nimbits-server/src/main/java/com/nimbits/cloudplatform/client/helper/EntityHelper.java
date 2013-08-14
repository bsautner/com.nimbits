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

package com.nimbits.cloudplatform.client.helper;


import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/9/12
 * Time: 2:21 PM
 *
 */
@Component("entityHelper")
public class EntityHelper {

    private static final int EXPIRE = 90;


    public Point createPointWithName(String nameStr)  {

        EntityName name = CommonFactory.createName(nameStr, EntityType.point);
        Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
        return PointModelFactory.createPointModel(entity, 0.0, EXPIRE, "", 0.0, false, false, false, 0, false, FilterType.fixedHysteresis, 0.1, false, PointType.basic, 0, false, 0.0);
    }
}
