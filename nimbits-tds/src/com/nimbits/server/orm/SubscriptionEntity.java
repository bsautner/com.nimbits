/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.subscription.*;

import javax.jdo.annotations.*;
import java.io.*;
import java.util.*;

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
    private Double maxRepeat;

    @Persistent
    private Date lastSent;

    @Persistent
    private Boolean notifyFormatJson;

    @Persistent
    private Boolean enabled;

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(Entity entity, String subscribedEntity, Integer notifyMethod, Integer subscriptionType, Double maxRepeat, Date lastSent, Boolean notifyFormatJson, Boolean enabled) throws NimbitsException {
        super(entity);
        this.subscribedEntity = subscribedEntity;
        this.notifyMethod = notifyMethod;
        this.subscriptionType = subscriptionType;
        this.maxRepeat = maxRepeat;
        this.lastSent = lastSent;
        this.notifyFormatJson = notifyFormatJson;
        this.enabled = enabled;
    }

    public SubscriptionEntity(final Subscription subscription) throws NimbitsException {
        super(subscription);
        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
        this.subscribedEntity = subscription.getSubscribedEntity();
    }



    @Override
    public String getKey() {
        return key.getName();
    }

    @Override
    public double getMaxRepeat() {
        return this.maxRepeat;
    }

    @Override
    public void setMaxRepeat(double maxRepeat) {
        this.maxRepeat = maxRepeat;
    }

    @Override
    public Date getLastSent() {
        return lastSent;
    }

    @Override
    public void setLastSent(Date lastSent) {
        this.lastSent = lastSent;
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
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        Subscription s = (Subscription)update;
        notifyMethod = (s.getNotifyMethod().getCode());
        subscriptionType = (s.getSubscriptionType().getCode());
        lastSent = (s.getLastSent());
        maxRepeat = (s.getMaxRepeat());
        enabled = (s.getEnabled());
        notifyFormatJson = (s.getNotifyFormatJson());
    }
}
