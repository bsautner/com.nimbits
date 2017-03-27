/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.transaction.entity;

import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.instance.InstanceModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.schedule.ScheduleModel;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.summary.SummaryModel;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.sync.SyncModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.server.orm.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class EntityHelper {


    public static Entity downcastEntity(final Entity entity) {

        switch (entity.getEntityType()) {


            case user:
                return new UserEntity((User) entity);

            case point:

                return new PointEntity((Point) entity);

            case category:
                return new CategoryEntity(entity);

            case subscription:
                return new SubscriptionEntity((Subscription) entity);

            case calculation:
                return new CalcEntity((Calculation) entity);


            case summary:
                return new SummaryEntity((Summary) entity);

            case sync:
                return new SyncEntity((Sync) entity);

            case schedule:
                return new ScheduleEntity((Schedule) entity);

            case instance:
                return new InstanceEntity((Instance) entity);

            case webhook:
                return new WebHookEntity((WebHook) entity);

            default:
                throw new IllegalArgumentException("Attempt to downcast an unknown entity");
        }


    }


    public static List<Entity> createModels(final User user, final Collection<Entity> entity) {
        final List<Entity> retObj = new ArrayList<>(entity.size());
        for (final Entity e : entity) {
            if (e.getEntityType() != null && e.getEntityType().isTreeGridItem()) {

                Entity r = createModel(user, e);
                retObj.add(r);


            }
        }
        return retObj;

    }

    public static Entity createModel(final User user, final Entity entity) {
        final Entity model;


        switch (entity.getEntityType()) {

            case user:
                model = new UserModel.Builder().init((User) entity).create();
                break;
            case point:
                model = new PointModel.Builder().init((Point) entity).create();
                break;
            case category:
                model = new CategoryModel.Builder().init((Category) entity).create();
                break;
            case subscription:
                model = new SubscriptionModel.Builder().init((Subscription) entity).create();
                break;
            case calculation:
                model = new CalculationModel.Builder().init((Calculation) entity).create();
                break;

            case summary:
                model = new SummaryModel.Builder().init((Summary) entity).create();
                break;
            case sync:
                model = new SyncModel.Builder().init((Sync) entity).create();
                break;

            case instance:
                model = new InstanceModel.Builder().init((Instance) entity).create();// InstanceModelFactory.createInstance((Instance) entity);
                break;
            case schedule:
                model = new ScheduleModel.Builder().init((Schedule) entity).create();
                break;
            case webhook:
                model = new WebHookModel.Builder().init((WebHook) entity).create();
                break;
            default:
                model = null;
                break;

        }


        return model;

    }
}
