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

package com.nimbits.server.admin.legacy.orm;

import com.google.appengine.api.datastore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.Entity;
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

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class SubscriptionEntity implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key key;

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

    public SubscriptionEntity(final Entity entity, final Subscription subscription) {

        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
        this.subscribedEntity = subscription.getSubscribedEntity();
        this.key =  KeyFactory.createKey(SubscriptionEntity.class.getSimpleName(), entity.getKey());
    }




    public String getKey() {
      return key.getName();
    }


    public double getMaxRepeat() {
        return this.maxRepeat;
    }


    public void setMaxRepeat(double maxRepeat) {
      this.maxRepeat = maxRepeat;
    }


    public Date getLastSent() {
      return lastSent;
    }


    public void setLastSent(Date lastSent) {
       this.lastSent = lastSent;
    }

    public String getSubscribedEntity() {
        return subscribedEntity;
    }

    public void setSubscribedEntity(String subscribedEntity) {
        this.subscribedEntity = subscribedEntity;
    }

    public boolean getNotifyFormatJson() {
        return notifyFormatJson;
    }


    public void setNotifyFormatJson(boolean notifyFormatJson) {
        this.notifyFormatJson = notifyFormatJson;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SubscriptionNotifyMethod getNotifyMethod() {
        return SubscriptionNotifyMethod.get(this.notifyMethod);
    }

    public void setNotifyMethod(SubscriptionNotifyMethod notifyMethod) {
        this.notifyMethod = notifyMethod.getCode();
    }

    public SubscriptionType getSubscriptionType() {
        return SubscriptionType.get(subscriptionType);
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType.getCode();
    }


}
