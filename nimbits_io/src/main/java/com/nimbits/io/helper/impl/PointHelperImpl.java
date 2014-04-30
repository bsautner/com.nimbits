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
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.helper.PointHelper;

import java.util.List;



public class PointHelperImpl extends EntityHelperImpl implements PointHelper {




    public PointHelperImpl(Server server, EmailAddress email, String accessKey) {
        super(server, email, accessKey);

    }




    @Override
    public Point createPoint(final String name, final String description)   {

        EntityName pointName = CommonFactory.createName(name, EntityType.point);

        Entity entity = EntityModelFactory.createEntity(pointName, description, EntityType.point, ProtectionLevel.everyone,
                email.getValue(),  email.getValue());

        Point point = PointModelFactory.createPointModel(entity, 0.0, 365, "", 0.0, false, false, false, 0, false, FilterType.none,
                0.0, false, PointType.basic, 0, false, 0.0);

        List<Point> sample;

        sample = nimbitsClient.addEntity(point, PointModel.class);

        if (sample.isEmpty()) {
            if (pointExists(name)) {
                throw new IllegalStateException("Creating point failed - point already exists");
            }
            else {
                throw new IllegalStateException("Creating point failed");
            }
        }
        else {
            return sample.get(0);
        }


    }

    @Override
    public boolean pointExists(final String name)   {

        SimpleValue<String> id = SimpleValue.getInstance(email + "/" + name);
        List<Entity> sample;

        sample = nimbitsClient.getEntity(id, EntityType.point, PointModel.class);

        return ! sample.isEmpty();



    }

    @Override
    public Point getPoint(final String name)   {
        SimpleValue<String> id;
        if (! name.startsWith(email.getValue())) {
           id = SimpleValue.getInstance(email + "/" + name);
        }
        else {
            id = SimpleValue.getInstance(name);
        }

        List<Point> sample;

        sample = nimbitsClient.getEntity(id, EntityType.point, PointModel.class);

        if (sample.isEmpty()) {
            throw new IllegalStateException("Point Not Found");
        }
        else {
            return sample.get(0);
        }



    }


}
