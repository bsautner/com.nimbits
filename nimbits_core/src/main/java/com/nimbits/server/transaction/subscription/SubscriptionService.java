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

package com.nimbits.server.transaction.subscription;

import com.nimbits.client.enums.Action;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transaction.BaseProcessor;
import com.nimbits.server.transaction.entity.dao.EntityDao;

import java.util.List;


public interface SubscriptionService extends BaseProcessor {




    void sendGCM(final User user, Subscription subscription, Point points, Action action);

    List<Entity> sendSocket(EntityDao entityDao, User user, List<Point> points);

    void processIncomingSocketValues(EntityDao entityDao, User user, Point point);
}
