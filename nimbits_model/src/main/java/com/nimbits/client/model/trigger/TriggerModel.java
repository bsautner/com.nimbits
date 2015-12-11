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

package com.nimbits.client.model.trigger;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;


public abstract class TriggerModel extends EntityModel implements Serializable, Trigger {

    @Expose
    private String target;

    @Expose
    private String trigger;

    @Expose
    private boolean enabled;

    @SuppressWarnings("unused")
    protected TriggerModel() {
    }



    public TriggerModel(String key, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String uuid, String target, String trigger, boolean enabled) {
        super(key, name, description, entityType, protectionLevel, parent, owner, uuid);
        this.target = target;
        this.trigger = trigger;
        this.enabled = enabled;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getTrigger() {
        return trigger;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }


    public static abstract class TriggerBuilder<T> extends EntityBuilder {


        protected String target;


        protected String trigger;


        protected boolean enabled;

        public abstract T target(String v);

        public abstract T trigger(String v);

        public abstract T enabled(boolean v);


    }
}
