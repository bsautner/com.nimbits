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

package com.nimbits.client.model.subscription;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;


public class SubscriptionModel extends EntityModel implements Serializable, Subscription {

    @Expose
    private String subscribedEntity;
    @Expose
    private int notifyMethod;
    @Expose
    private int subscriptionType;

    @Expose
    private String target;
    @Expose
    private boolean enabled;

    @SuppressWarnings("unused")
    private SubscriptionModel() {
    }


    public SubscriptionModel(String id, CommonIdentifier name, String description, EntityType entityType, String parent, String owner, String subscribedEntity, int notifyMethod, int subscriptionType, String target, boolean enabled) {
        super(id, name, description, entityType,  parent, owner);
        this.subscribedEntity = subscribedEntity;
        this.notifyMethod = notifyMethod;
        this.subscriptionType = subscriptionType;
        this.target = target;
        this.enabled = enabled;
    }

    @Override
    public String getSubscribedEntity() {
        return this.subscribedEntity;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public SubscriptionNotifyMethod getNotifyMethod() {
        return SubscriptionNotifyMethod.get(this.notifyMethod);
    }

    @Override
    public void setNotifyMethod(SubscriptionNotifyMethod notifyMethod) {
        this.notifyMethod = notifyMethod.getCode();
    }

    @Override
    public SubscriptionType getSubscriptionType() {
        return SubscriptionType.get(subscriptionType);
    }

    @Override
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType.getCode();
    }

    @Override
    public String getTarget() {
        return target == null ? "" : target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }


    public static class Builder extends EntityBuilder {

        private final EntityType type = EntityType.subscription;

        private String subscribedEntity;

        private SubscriptionNotifyMethod notifyMethod;

        private SubscriptionType subscriptionType;

        private int maxRepeat;

        private String target;

        private boolean enabled;

        public Builder subscribedEntity(String subscribedEntity) {
            this.subscribedEntity = subscribedEntity;
            return this;
        }

        public Builder subscribedEntity(Entity subscribedEntity) {
            this.subscribedEntity = subscribedEntity.getId();
            return this;
        }

        public Builder notifyMethod(SubscriptionNotifyMethod notifyMethod) {
            this.notifyMethod = notifyMethod;
            return this;
        }

        public Builder subscriptionType(SubscriptionType subscriptionType) {
            this.subscriptionType = subscriptionType;
            return this;
        }

        public Builder maxRepeat(int maxRepeat) {
            this.maxRepeat = maxRepeat;
            return this;
        }

        public Builder target(String target) {
            this.target = target;
            return this;
        }

        public Builder target(Entity target) {
            this.target = target.getId();
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }


        public Subscription create() {
            if (subscriptionType == null) {
                subscriptionType = SubscriptionType.none;
            }
            if (notifyMethod == null) {
                notifyMethod = SubscriptionNotifyMethod.none;
            }
            return new SubscriptionModel(id, name, description, type,  parent
            , owner, subscribedEntity, notifyMethod.getCode(), subscriptionType.getCode(), target , enabled );

        }


        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }
  @Override
        public Builder alertType(int alertType) {
            this.alertType = alertType;
            return this;
        }
        @Override
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }
        @Override
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }
        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder init(Subscription entity) {
            super.init(entity);
            this.subscribedEntity = entity.getSubscribedEntity();
            this.notifyMethod = entity.getNotifyMethod();
            this.subscriptionType = entity.getSubscriptionType();
            this.target = entity.getTarget();
            this.enabled = entity.getEnabled();
            return this;
        }


    }
}
