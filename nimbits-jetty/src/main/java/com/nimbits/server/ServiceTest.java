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

package com.nimbits.server;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.server.orm.CategoryEntity;

import javax.jdo.PersistenceManagerFactory;

/**
 * Created by benjamin on 10/11/13.
 */
public class ServiceTest {

    public void writeToDb() {


        PersistenceManagerFactory pmf = PMF.get();
//        ServerSetting setting = new ServerSetting("test", "test setting");
//        pmf.getPersistenceManager().makePersistent(setting);
//        pmf.close();


        Entity entity = EntityModelFactory.createEntity("foo", "des", EntityType.category, ProtectionLevel.everyone, "foo@bar.com", "foo@bar.com");
        CategoryEntity store = new CategoryEntity(entity);
        pmf.getPersistenceManager().makePersistent(store);
        pmf.close();
    }

}
