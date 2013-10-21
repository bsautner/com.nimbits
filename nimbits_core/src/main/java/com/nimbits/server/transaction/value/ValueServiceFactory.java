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

package com.nimbits.server.transaction.value;

import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.value.cache.ValueCache;
import com.nimbits.server.transaction.value.cache.ValueCacheImpl;
import com.nimbits.server.transaction.value.dao.ValueDao;
import com.nimbits.server.transaction.value.dao.ValueDaoImpl;
import com.nimbits.server.transaction.value.service.ValueService;
import com.nimbits.server.transaction.value.service.ValueServiceImpl;

/**
 * Created by benjamin on 10/14/13.
 */
public class ValueServiceFactory {

    public static ValueService getInstance(NimbitsEngine engine, TaskService taskService) {

         return new ValueServiceImpl(engine, taskService);
    }


    public static ValueCache getCacheInstance(NimbitsEngine engine, TaskService taskService) {
        return new ValueCacheImpl(engine, taskService);

    }
    public static ValueDao getDaoInstance(NimbitsEngine engine) {
        return new ValueDaoImpl(engine);

    }
}
