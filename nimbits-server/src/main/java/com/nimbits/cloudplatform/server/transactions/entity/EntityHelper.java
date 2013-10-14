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

package com.nimbits.cloudplatform.server.transactions.entity;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKeyModel;
import com.nimbits.cloudplatform.client.model.calculation.Calculation;
import com.nimbits.cloudplatform.client.model.calculation.CalculationModel;
import com.nimbits.cloudplatform.client.model.category.CategoryModel;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.subscription.Subscription;
import com.nimbits.cloudplatform.client.model.subscription.SubscriptionModel;
import com.nimbits.cloudplatform.client.model.summary.Summary;
import com.nimbits.cloudplatform.client.model.summary.SummaryModel;
import com.nimbits.cloudplatform.client.model.user.UserModel;
import com.nimbits.cloudplatform.server.orm.*;

/**
 * Empathy Lab
 * User: benjamin
 * Date: 1/4/13
 * Time: 12:47 PM
 */
public class EntityHelper {


    @SuppressWarnings({"OverlyCoupledMethod", "OverlyLongMethod", "OverlyComplexMethod"})
    public static Entity downcastEntity(final Entity entity) {
        Entity commit;
        switch (entity.getEntityType()) {


            case user:
                commit = new UserEntity(entity);
                break;
            case point:

                commit = new PointEntity((Point) entity);

                break;
            case category:
                commit = new CategoryEntity(entity);
                break;
            case subscription:
                commit = new SubscriptionEntity((Subscription) entity);
                break;
            case calculation:
                commit = new CalcEntity((Calculation) entity);
                break;

            case summary:
                commit = new SummaryEntity((Summary) entity);
                break;

            case accessKey:
                commit = new AccessKeyEntity((AccessKey) entity);
                break;
            default:
                commit = new CategoryEntity(entity);
        }

        return commit;
    }

    public static Class getClass(EntityType type) {
        switch (type) {


            case user:
                return UserModel.class;
            case point:
                return PointModel.class;
            case category:
                return CategoryModel.class;
            case subscription:
                return SubscriptionModel.class;
            case calculation:
                return CalculationModel.class;

            case summary:
                return SummaryModel.class;
            case accessKey:
                return AccessKeyModel.class;
            default:
                return null;
        }

    }
}
