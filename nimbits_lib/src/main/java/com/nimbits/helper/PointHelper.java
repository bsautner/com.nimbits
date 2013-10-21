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

package com.nimbits.helper;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.simple.SimpleValue;
import com.nimbits.transaction.Transaction;
import com.nimbits.transaction.TransactionImpl;

import java.util.List;


/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 6:18 PM
 */
public class PointHelper {
//    public static DataPointHelper getInstance() {
//        return new DataPointHelper();
//    }
    private final String baseUrl;
    private final String email;
    private final Transaction transaction;
    public PointHelper(Server server, String email) {
        this.baseUrl = server.getUrl();
        this.email = email;
        transaction = new TransactionImpl(server, email);
    }


    public Point createDataPoint(final String name, final String description)   {

        EntityName pointName = CommonFactory.createName(name, EntityType.point);

        Entity entity = EntityModelFactory.createEntity(pointName, description, EntityType.point, ProtectionLevel.everyone,
                email,  email);

        Point point = PointModelFactory.createPointModel(entity, 0.0, 365, "", 0.0, false, false, false, 0, false, FilterType.none,
                0.0, false, PointType.basic, 0, false, 0.0);

        List<Point> sample;

            sample = transaction.addEntity(point, PointModel.class);

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

    public boolean pointExists(final String name)   {

        SimpleValue<String> id = SimpleValue.getInstance(email + "/" + name);
        List<Entity> sample;

            sample = transaction.getEntity(id, EntityType.point, PointModel.class);

        return ! sample.isEmpty();



    }

    public Point getPoint(final String name)   {

        SimpleValue<String> id = SimpleValue.getInstance(email + "/" + name);
        List<Point> sample;

        sample = transaction.getEntity(id, EntityType.point, PointModel.class);

        if (sample.isEmpty()) {
            throw new IllegalStateException("Point Not Found");
        }
        else {
            return sample.get(0);
        }



    }


}
