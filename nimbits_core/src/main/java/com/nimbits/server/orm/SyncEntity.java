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

package com.nimbits.server.orm;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.sync.Sync;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@SuppressWarnings("unused")
@PersistenceCapable
public class SyncEntity extends TriggerEntity implements Sync {

    @Persistent
    private String targetInstance;

    @Persistent
    private String accessKey;

    protected SyncEntity() {
    }

    public SyncEntity(final Sync s) {
        super(s);

        this.targetInstance = s.getTargetInstance();
        this.accessKey = s.getAccessKey();

    }


    @Override
    public void update(final Entity update) {
        super.update(update);
        final Sync summary = (Sync) update;

        this.targetInstance = summary.getTargetInstance();
        this.accessKey = summary.getAccessKey();
    }

    @Override
    public String getTargetInstance() {
        return targetInstance;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }



}
