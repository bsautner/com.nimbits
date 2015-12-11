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

package com.nimbits.client.model.subscription;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
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
    private int maxRepeat;
    @Expose
    private String target;
    @Expose
    private boolean notifyFormatJson;
    @Expose
    private boolean enabled;

    @SuppressWarnings("unused")
    private SubscriptionModel() {
    }


    public SubscriptionModel(String key, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String uuid, String subscribedEntity, int notifyMethod, int subscriptionType, int maxRepeat, String target, boolean notifyFormatJson, boolean enabled) {
        super(key, name, description, entityType, protectionLevel, parent, owner, uuid);
        this.subscribedEntity = subscribedEntity;
        this.notifyMethod = notifyMethod;
        this.subscriptionType = subscriptionType;
        this.maxRepeat = maxRepeat;
        this.target = target;
        this.notifyFormatJson = notifyFormatJson;
        this.enabled = enabled;
    }

    @Override
    public String getSubscribedEntity() {
        return this.subscribedEntity;
    }

    @Override
    public void setSubscribedEntity(String uuid) {
        this.subscribedEntity = uuid;
    }

    @Override
    public boolean getNotifyFormatJson() {
        return this.notifyFormatJson;
    }

    @Override
    public void setNotifyFormatJson(boolean notifyFormatJson) {
        this.notifyFormatJson = notifyFormatJson;
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
    public int getMaxRepeat() {
        return maxRepeat;
    }

    @Override
    public void setMaxRepeat(int maxRepeat) {
        this.maxRepeat = maxRepeat;
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

        public Builder subscribedEntity(String subscribedEntity) {
            this.subscribedEntity = subscribedEntity;
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

        public Subscription create() {
            return new SubscriptionModel(key, name, description, type, protectionLevel, parent
            , owner, uuid, subscribedEntity, notifyMethod.getCode(), subscriptionType.getCode(), maxRepeat, target, true, true );

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
        public Builder key(String key) {
            this.key = key;
            return this;
        }
        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        @Override
        public Builder protectionLevel(ProtectionLevel protectionLevel) {
            this.protectionLevel = protectionLevel;
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
        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder init(Subscription entity) {
            initEntity(entity);
            this.subscribedEntity = entity.getSubscribedEntity();
            this.notifyMethod = entity.getNotifyMethod();
            this.subscriptionType = entity.getSubscriptionType();
            this.maxRepeat = entity.getMaxRepeat();
            this.target = entity.getTarget();
            return this;
        }

        private void initEntity(Entity anEntity) {

            this.key = anEntity.getKey();
            this.id = anEntity.getKey();
            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.entityType = anEntity.getEntityType();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();
            this.protectionLevel = anEntity.getProtectionLevel();
            this.alertType = anEntity.getAlertType().getCode();
            this.uuid = anEntity.getUUID();

        }
    }
}
