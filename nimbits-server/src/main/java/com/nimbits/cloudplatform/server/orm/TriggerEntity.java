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

package com.nimbits.cloudplatform.server.orm;

import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.trigger.Trigger;
import com.nimbits.cloudplatform.client.model.user.User;

import javax.jdo.annotations.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/19/12
 * Time: 12:20 PM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class TriggerEntity extends EntityStore implements Trigger {

    @Persistent
    String trigger;

    @Persistent
    String targetVar;

    @Persistent
    private Boolean enabled;

    public TriggerEntity(Entity entity)  {
        super(entity);
        this.trigger = ((Trigger)entity).getTrigger();
        this.targetVar =  ((Trigger)entity).getTarget();
        this.enabled = ((Trigger)entity).isEnabled();
    }
    public TriggerEntity(Entity anEntity, String trigger, String target, boolean enabled)  {
        super(anEntity);
        this.targetVar = target;
        this.trigger =trigger;
        this.enabled =enabled;
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
    public void validate(User user)   {
        super.validate(user);

    }

    @Override
    public void update(Entity update)  {
        super.update(update);
        Trigger t = (Trigger) update;
        this.enabled = t.isEnabled();
        this.targetVar = t.getTarget();
        this.trigger = t.getTrigger();
    }
}
