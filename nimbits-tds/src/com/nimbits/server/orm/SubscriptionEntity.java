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
public class SubscriptionEntity implements Serializable, Subscription {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String uuid;

    @Persistent
    private String subscribedEntity;

    @Persistent
    private Integer dataNotifyMethod = SubscriptionDeliveryMethod.none.getCode();

    @Persistent
    private Integer alertNotifyMethod = SubscriptionDeliveryMethod.none.getCode();

    @Persistent
    private Integer changeNotifyMethod = SubscriptionDeliveryMethod.none.getCode();

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

        this.dataNotifyMethod = subscription.getDataNotifyMethod().getCode();
        this.alertNotifyMethod = subscription.getAlertNotifyMethod().getCode();
        this.changeNotifyMethod = subscription.getChangeNotifyMethod().getCode();
        this.uuid = subscription.getUuid();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
        this.subscribedEntity = subscription.getSubscribedEntity();
    }

    @Override
    public SubscriptionDeliveryMethod getDataNotifyMethod() {
        return SubscriptionDeliveryMethod.get(dataNotifyMethod);
    }

    @Override
    public void setDataNotifyMethod(SubscriptionDeliveryMethod dataUpdateAlertMethod) {
        this.dataNotifyMethod = dataUpdateAlertMethod.getCode();
    }

    @Override
    public SubscriptionDeliveryMethod getAlertNotifyMethod() {
        return SubscriptionDeliveryMethod.get(alertNotifyMethod);
    }

    @Override
    public void setAlertNotifyMethod(SubscriptionDeliveryMethod alertNotifyMethod) {
        this.alertNotifyMethod = alertNotifyMethod.getCode();
    }

    @Override
    public SubscriptionDeliveryMethod getChangeNotifyMethod() {
        return SubscriptionDeliveryMethod.get(changeNotifyMethod);
    }

    @Override
    public void setChangeNotifyMethod(SubscriptionDeliveryMethod propertyChangeMethod) {
        this.changeNotifyMethod = propertyChangeMethod.getCode();
    }

    @Override
    public String getUuid() {
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
}
