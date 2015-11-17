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

package com.nimbits.io.helper.impl;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.helper.PointHelper;

@Deprecated //all helper classes are being replaced with the Nimbits.Builder() client
public class PointHelperImpl extends EntityHelperImpl implements PointHelper {


    public PointHelperImpl(Server server) {
        super(server);

    }


    @Override
    public Point createPoint(final String name, final String description) {

        EntityName pointName = CommonFactory.createName(name, EntityType.point);

        Entity entity = EntityModelFactory.createEntity(pointName, description, EntityType.point, ProtectionLevel.everyone,
                server.getEmail().getValue(), server.getEmail().getValue());

        Point point = PointModelFactory.createPointModel(entity, 0.0, 365, "", 0.0, false, false, false, 0, false, FilterType.none,
                0.0, false, PointType.basic, 0, false, 0.0, 10);


        return (Point) nimbitsClient.addEntity(point);


    }

    @Override
    public boolean pointExists(final String name) {

        SimpleValue<String> id = SimpleValue.getInstance(server.getEmail().getValue() + "/" + name);
        Entity sample;
        try {
            sample = nimbitsClient.getEntity(id, EntityType.point);
        } catch (Throwable ex) {
            return false;
        }

        return sample != null;


    }

    @Override
    public Point getPoint(final String name) {

        SimpleValue<String> id;
        if (!name.startsWith(server.getEmail().getValue())) {
            id = SimpleValue.getInstance(server.getEmail().getValue() + "/" + name);
        } else {
            id = SimpleValue.getInstance(name);
        }

        return (Point) nimbitsClient.getEntity(id, EntityType.point);

    }


}
