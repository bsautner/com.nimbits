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

package com.nimbits.transaction;

import com.google.common.collect.Range;
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.simple.SimpleValue;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.Date;
import java.util.List;

/**
 * Created by benjamin on 10/20/13.
 */
public interface Transaction {


    List<User> getSession();

    List<User> getSession(String email, String key);

    List<Value> getValue(Entity entity);

    <T> List<T> getTree();

    List<Value> postValue(Entity entity, Value value);

    List<Value> getSeries(String entity);

    List<Value> getSeries(String entity, Range<Date> range);

    void deleteEntity(Entity entity);

    <T, K> List<T> addEntity(Entity entity, Class<K> clz);

    <T> List<T> updateEntity(Entity entity, Class<T> clz);

    <T, K> List<T> getEntity(SimpleValue<String> entityId, EntityType type, Class<K> clz);

    List<AndroidControl> getControl();

    void doHeartbeat();
}
