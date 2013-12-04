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

package com.nimbits.client.model.sync;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.trigger.TriggerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:02 AM
 */
public class SyncModelFactory {

    private SyncModelFactory() {
    }

    public static SyncModel createSync(Sync model)  {
        return new SyncModel(model);
    }


    public static SyncModel createSync(
            final Entity e,
            final TriggerEntity entity,
            final String targetInstance,
            final String targetPoint )  {
        return new SyncModel(e, entity, targetInstance, targetPoint);

    }

}
