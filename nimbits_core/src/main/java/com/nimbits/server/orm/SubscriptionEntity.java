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

package com.nimbits.server.orm;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.subscription.Subscription;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:47 PM
 */

@PersistenceCapable()
public class SubscriptionEntity extends EntityStore implements Serializable, Subscription {

    @Persistent
    private String subscribedEntity;

    @Persistent
    private Integer notifyMethod = SubscriptionNotifyMethod.none.getCode();

    @Persistent
    private Integer subscriptionType = SubscriptionType.none.getCode();

    @Persistent
    private Double maxRepeat; //todo migrate to int

    @Persistent
    private Boolean notifyFormatJson;

    @Persistent
    private Boolean enabled;

    @SuppressWarnings("unused")
    protected SubscriptionEntity() {

    }



    public SubscriptionEntity(final Subscription subscription)  {
        super(subscription);
        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();
        this.maxRepeat = (double) subscription.getMaxRepeat();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
        this.subscribedEntity = subscription.getSubscribedEntity();
    }


    @Override
    public int getMaxRepeat() {
        return this.maxRepeat == null ? 0 : (int)Math.round(maxRepeat);
    }

    @Override
    public void setMaxRepeat(int maxRepeat) {
        this.maxRepeat = (double) maxRepeat;
    }

    @Override
    public String getSubscribedEntity() {
        return subscribedEntity;
    }
    @Override
    public void setSubscribedEntity(String subscribedEntity) {
        this.subscribedEntity = subscribedEntity;
    }
    @Override
    public boolean getNotifyFormatJson() {
        return notifyFormatJson;
    }

    @Override
    public void setNotifyFormatJson(boolean notifyFormatJson) {
        this.notifyFormatJson = notifyFormatJson;
    }
    @Override
    public boolean getEnabled() {
        return enabled;
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
    public void update(Entity update)  {
        super.update(update);
        Subscription s = (Subscription)update;
        notifyMethod = s.getNotifyMethod().getCode();
        subscriptionType = s.getSubscriptionType().getCode();
        maxRepeat = (double) s.getMaxRepeat();
        enabled = s.getEnabled();
        notifyFormatJson = s.getNotifyFormatJson();
    }
}
