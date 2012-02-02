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
    private String subscriberUUID;

    @Persistent
    private String subscribedPointUUID;

    @Persistent
    private Long categoryId;

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

    public SubscriptionEntity(final String subscriberUUID) {
        this.subscriberUUID = subscriberUUID;
    }

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(final Subscription subscription) {
        this.subscriberUUID = subscription.getSubscriberUUID();
        this.dataUpdateAlertMethod = subscription.getDataUpdateAlertMethod().getCode();
        this.alertStateChangeMethod = subscription.getAlertStateChangeMethod().getCode();
        this.propertyChangeMethod = subscription.getPropertyChangeMethod().getCode();
        this.subscribedPointUUID = subscription.getSubscribedPointUUID();
        this.categoryId = subscription.getCategoryId();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
    }

    @Override
    public String getSubscriberUUID() {
        return subscriberUUID;
    }

    @Override
    public void setSubscriberUUID(String subscriberUUID) {
        this.subscriberUUID = subscriberUUID;
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
    public String getSubscribedPointUUID() {
      return this.subscribedPointUUID;
    }

    @Override
    public void setSubscribedPointUUID(String uuid) {
      this.subscribedPointUUID = uuid;
    }


    @Override
    public long getCategoryId() {
      return this.categoryId;
    }

    @Override
    public void setCategoryId(long id) {
      this.categoryId = id;
    }

    @Override
    public double getMaxRepeat() {
        return 0;  //auto generated
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

}
