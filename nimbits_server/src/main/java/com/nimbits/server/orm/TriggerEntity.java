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
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.*;


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class TriggerEntity extends EntityStore implements Trigger {

    @Persistent
    private String trigger;

    @Persistent
    private String targetVar;

    @Persistent
    private Boolean enabled;

    public TriggerEntity(Entity entity) {
        super(entity);
        this.trigger = ((Trigger) entity).getTrigger();
        this.targetVar = ((Trigger) entity).getTarget();
        this.enabled = ((Trigger) entity).isEnabled();
    }



    public TriggerEntity() {
    }

    @Override
    public String getTrigger() {
        return trigger;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled == null ? true : this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setTarget(String target) {
        this.targetVar = target;
    }

    @Override
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }


    @Override
    public String getTarget() {
        return targetVar;
    }

    @Override
    public void validate(User user) {
        super.validate(user);
        if (targetVar.equals(trigger)) {
            throw new IllegalArgumentException("Infinite Recursion, a target point cannot be the trigger.");
        }


    }


    @Override
    public void update(Entity update) {
        super.update(update);
        Trigger t = (Trigger) update;
        this.enabled = t.isEnabled();
        this.targetVar = t.getTarget();
        this.trigger = t.getTrigger();
    }
}
