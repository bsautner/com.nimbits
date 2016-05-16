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

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.subscription.Subscription;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;


@PersistenceCapable()
public class SubscriptionEntity extends EntityStore implements Serializable, Subscription {

    @Persistent
    private String subscribedEntity;

    @Persistent
    private Integer notifyMethod = SubscriptionNotifyMethod.none.getCode();

    @Persistent
    private Integer subscriptionType = SubscriptionType.none.getCode();

    @Persistent
    private Boolean enabled;

    @Persistent
    private String target;

    @SuppressWarnings("unused")
    protected SubscriptionEntity() {

    }


    public SubscriptionEntity(final Subscription subscription) {
        super(subscription);
        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();

        this.enabled = subscription.getEnabled();
        this.subscribedEntity = subscription.getSubscribedEntity();
        this.target = subscription.getTarget();
    }

    @Override
    public String getSubscribedEntity() {
        return subscribedEntity;
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
    public String getTarget() {
        return target == null ? "" : target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public void update(Entity update) {
        super.update(update);
        Subscription s = (Subscription) update;
        notifyMethod = s.getNotifyMethod().getCode();
        subscriptionType = s.getSubscriptionType().getCode();

        enabled = s.getEnabled();

        target = s.getTarget();
        subscribedEntity = s.getSubscribedEntity();
    }

    @Override
    public void init(Entity anEntity) {

    }
}
