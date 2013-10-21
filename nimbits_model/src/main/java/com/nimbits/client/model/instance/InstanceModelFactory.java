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

package com.nimbits.client.model.instance;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:57 PM
 */
public class InstanceModelFactory {

    private InstanceModelFactory() {
    }

    public static Instance createInstance(final Instance server)  {

        return new InstanceModel(server);

    }
      public static Instance createInstance(final Entity baseEntity, final String baseUrl, final EmailAddress ownerEmail, final String serverVersion)  {
         return new InstanceModel(baseEntity, baseUrl, ownerEmail, serverVersion);

    }

}
