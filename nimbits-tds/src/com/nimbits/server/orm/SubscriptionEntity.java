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

import com.nimbits.client.enums.SubscriptionNotifyMethod;
import com.nimbits.client.enums.SubscriptionType;
import com.nimbits.client.model.subscription.Subscription;

import javax.jdo.annotations.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:47 PM
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class SubscriptionEntity implements Serializable, Subscription {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String uuid;

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

    public SubscriptionEntity(final Subscription subscription) {

        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();
        this.uuid = subscription.getKey();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
        this.subscribedEntity = subscription.getSubscribedEntity();
    }



    @Override
    public String getKey() {
      return this.uuid;
    }

    @Override
    public void setUuid(String uuid) {
      this.uuid = uuid;
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
}
