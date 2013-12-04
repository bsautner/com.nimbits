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

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/19/12
 * Time: 12:30 PM
 */
public class TriggerModel extends EntityModel implements Serializable, Trigger {

    private String target;
    private String trigger;
    private boolean enabled;

    @SuppressWarnings("unused")
    protected TriggerModel() {
    }

    public TriggerModel(final Trigger anEntity) {
        super(anEntity);
        this.target = anEntity.getTarget();
        this.trigger = anEntity.getTrigger();
        this.enabled = anEntity.isEnabled();
    }

    public TriggerModel(final Entity anEntity, final TriggerEntity trigger, final TargetEntity target, boolean enabled) {
        super(anEntity);
        if (target != null) {
            this.target = target.getValue();
        }
        this.trigger = trigger.getValue();
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
}
