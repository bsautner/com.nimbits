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

package com.nimbits.client.helper;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/9/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityHelper {

    private static final int EXPIRE = 90;
    public static Point createPointWithName(String nameStr) throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(nameStr, EntityType.point);
        Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
        return PointModelFactory.createPointModel(entity, 0.0, EXPIRE, "", 0.0, false, false, false, 0, false, FilterType.fixedHysteresis, 0.1, false, PointType.basic, 0, false, 0.0);
    }
}
