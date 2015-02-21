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
import com.nimbits.client.model.trigger.TriggerModel;


public class SyncModel extends TriggerModel implements Sync {

    private String targetInstance;
    private String targetPoint;
    private String accessKey;

    @Override
    public String getTargetInstance() {
        return targetInstance;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public void setTargetInstance(String targetInstance) {
        this.targetInstance = targetInstance;
    }

    @Override
    public String getTargetPoint() {
        return targetPoint;
    }

    @Override
    public void setTargetPoint(String targetPoint) {
        this.targetPoint = targetPoint;
    }

    public SyncModel(
            final Entity entity,
            final TriggerEntity trigger,
            final String targetInstance,
            final String targetPoint,
            final String accessKey) {
        super(entity, trigger, null, true);
        this.targetInstance = targetInstance;
        this.targetPoint = targetPoint;
        this.accessKey = accessKey;

    }

    public SyncModel(Sync sync) {
        super(sync);
        this.targetPoint = sync.getTargetPoint();
        this.targetInstance = sync.getTargetInstance();
        this.accessKey = sync.getAccessKey();


    }

    @SuppressWarnings("unused")
    public SyncModel() {
    }


}
