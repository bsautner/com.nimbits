/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.server.orm.validation.RecursionValidation;

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

    public TriggerEntity(Entity entity) throws NimbitsException {
        super(entity);
        this.trigger = ((Trigger)entity).getTrigger();
        this.targetVar =  ((Trigger)entity).getTarget();
        this.enabled = ((Trigger)entity).isEnabled();
    }
    public TriggerEntity(Entity anEntity, String trigger, String target, boolean enabled) throws NimbitsException {
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
    public void validate() throws NimbitsException {
        super.validate();
        if (enabled) {
        RecursionValidation.validate(this);
        }
    }

    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        Trigger t = (Trigger) update;
        this.enabled = t.isEnabled();
        this.targetVar = t.getTarget();
        this.trigger = t.getTrigger();
    }
}
