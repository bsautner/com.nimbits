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

package com.nimbits.io;

import com.google.common.collect.Range;
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Basic Input and Output for the Nimbits API.
 * @see com.nimbits.client.model package for the varios POJOs this api returns.
 */

public interface NimbitsClient {


    /**
     * Returns the authenticated user for that session.  Response is a JSON formated User object in a list.
     * @see com.nimbits.client.model.user.User

     * @return A list with a valid user or an empty list if authentication fails.
     */
    User getSession();

    List<Value> getValue(Entity entity);

    Map<String, Integer> moveCron();

    List<Entity> getTree();

    List<Value> postValue(Entity entity, Value value);

    List<Value> getSeries(String entity);

    List<Value> getSeries(String entity, Range<Date> range);

    List<Value> getSeries(String entity, int count);

    void deleteEntity(Entity entity);

    <T, K> List<T> addEntity(Entity entity, Class<K> clz);

    <T> List<T> updateEntity(Entity entity, Class<T> clz);

    <T, K> List<T> getEntity(SimpleValue<String> entityId, EntityType type, Class<K> clz);

    List<AndroidControl> getControl();

    void doHeartbeat(Entity parent);

    void recordSeries(Point entity);

    void recordSeries(List<Point> entity);
}
