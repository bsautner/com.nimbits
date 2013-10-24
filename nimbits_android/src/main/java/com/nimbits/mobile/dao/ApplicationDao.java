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

package com.nimbits.mobile.dao;

import android.database.Cursor;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;

import java.util.List;

/**
 * Created by benjamin on 10/20/13.
 */
public interface ApplicationDao {
    Server getServer();

    int getCount(long id);

    void setDefaultInstanceUrl(long id);

    int storeTree(long id, List<Entity> entities, boolean refresh);

    Cursor getChildren(Entity entity);

    Entity getEntity(long id);

    Entity getParent(long id);

    long getIdByName(String name);

    long getParentId(long id);

    void updateValue(long id, Value value);


    Value getValue(long currentEntity);
}
