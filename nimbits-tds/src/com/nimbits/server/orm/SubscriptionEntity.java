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
    private String UUID;

    @Persistent
    private String subscribedEntityUUID;

    @Persistent
    private Integer dataUpdateAlertMethod = SubscriptionDeliveryMethod.none.getCode();

    @Persistent
    private Integer alertStateChangeMethod = SubscriptionDeliveryMethod.none.getCode();

    @Persistent
    private Integer propertyChangeMethod = SubscriptionDeliveryMethod.none.getCode();

    @Persistent
    private Double maxRepeat;

    @Persistent
    private Date lastSent;


    public SubscriptionEntity() {
    }

    public SubscriptionEntity(final Subscription subscription) {

        this.dataUpdateAlertMethod = subscription.getDataUpdateAlertMethod().getCode();
        this.alertStateChangeMethod = subscription.getAlertStateChangeMethod().getCode();
        this.propertyChangeMethod = subscription.getPropertyChangeMethod().getCode();
        this.UUID = subscription.getUUID();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
    }

    @Override
    public SubscriptionDeliveryMethod getDataUpdateAlertMethod() {
        return SubscriptionDeliveryMethod.get(dataUpdateAlertMethod);
    }

    @Override
    public void setDataUpdateAlertMethod(SubscriptionDeliveryMethod dataUpdateAlertMethod) {
        this.dataUpdateAlertMethod = dataUpdateAlertMethod.getCode();
    }

    @Override
    public SubscriptionDeliveryMethod getAlertStateChangeMethod() {
        return SubscriptionDeliveryMethod.get(alertStateChangeMethod);
    }

    @Override
    public void setAlertStateChangeMethod(SubscriptionDeliveryMethod alertStateChangeMethod) {
        this.alertStateChangeMethod = alertStateChangeMethod.getCode();
    }

    @Override
    public SubscriptionDeliveryMethod getPropertyChangeMethod() {
        return SubscriptionDeliveryMethod.get(propertyChangeMethod);
    }

    @Override
    public void setPropertyChangeMethod(SubscriptionDeliveryMethod propertyChangeMethod) {
        this.propertyChangeMethod = propertyChangeMethod.getCode();
    }

    @Override
    public String getUUID() {
      return this.UUID;
    }

    @Override
    public void setUUID(String uuid) {
      this.UUID = uuid;
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

    public String getSubscribedEntityUUID() {
        return subscribedEntityUUID;
    }

    public void setSubscribedEntityUUID(String subscribedEntityUUID) {
        this.subscribedEntityUUID = subscribedEntityUUID;
    }
}
