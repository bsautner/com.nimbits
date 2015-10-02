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

    public SubscriptionModel(Subscription subscription) {
        super(subscription);
        this.subscribedEntity = subscription.getSubscribedEntity();
        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
        this.target = subscription.getTarget();

    }

    public SubscriptionModel(
            EntityName name,
            String parent,
            String subscribedEntity,
            SubscriptionType subscriptionType,
            SubscriptionNotifyMethod subscriptionNotifyMethod,
            int maxRepeat,
            boolean formatJson,
            boolean enabled,
            String target) {
        super(name, "", EntityType.subscription, ProtectionLevel.everyone, parent, "", "");
        this.subscribedEntity = subscribedEntity;
        this.subscriptionType = subscriptionType.getCode();
        this.notifyMethod = subscriptionNotifyMethod.getCode();
        this.maxRepeat = maxRepeat;
        this.enabled = enabled;
        this.notifyFormatJson = formatJson;
        this.target = target;
    }


    public SubscriptionModel(
            Entity entity,
            String subscribedEntity,
            SubscriptionType subscriptionType,
            SubscriptionNotifyMethod subscriptionNotifyMethod,
            int maxRepeat,
            boolean formatJson,
            boolean enabled,
            String target) {
        super(entity);
        this.subscribedEntity = subscribedEntity;
        this.subscriptionType = subscriptionType.getCode();
        this.notifyMethod = subscriptionNotifyMethod.getCode();
        this.maxRepeat = maxRepeat;
        this.enabled = enabled;
        this.notifyFormatJson = formatJson;
        this.target = target;
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
            return new SubscriptionModel(name, parent, subscribedEntity, subscriptionType, notifyMethod, maxRepeat, true, true, target);

        }


        public Builder name(String name) {
            this.name = CommonFactory.createName(name, EntityType.subscription);
            return this;
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        @Override
        public Builder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }
    }
}
