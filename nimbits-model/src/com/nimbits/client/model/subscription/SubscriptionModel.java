package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:02 PM
 */
public class SubscriptionModel implements Serializable, Subscription  {

    private String key;
    private String subscriberUUID;
    private String subscribedPointUUID;
    private int dataUpdateAlertMethod;
    private int alertAlertChangeMethod;
    private int propertyChangeMethod;
    private long categoryId;
    private double maxRepeat;
    private Date lastSent;

    public SubscriptionModel() {
    }

    public SubscriptionModel(Subscription subscription) {
        this.subscriberUUID = subscription.getSubscriberUUID();
        this.subscribedPointUUID = subscription.getSubscribedPointUUID();
        this.dataUpdateAlertMethod = subscription.getDataUpdateAlertMethod().getCode();
        this.alertAlertChangeMethod = subscription.getAlertStateChangeMethod().getCode();
        this.propertyChangeMethod = subscription.getPropertyChangeMethod().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.categoryId = subscription.getCategoryId();

    }

    public SubscriptionModel(SubscriptionDeliveryMethod dataUpdateAlertMethod,
                             SubscriptionDeliveryMethod alertStateChangeMethod,
                             SubscriptionDeliveryMethod propertyChangeMethod,
                             double maxRepeat,
                             Date lastSent) {

        this.dataUpdateAlertMethod = dataUpdateAlertMethod.getCode();
        this.alertAlertChangeMethod = alertStateChangeMethod.getCode();
        this.propertyChangeMethod = propertyChangeMethod.getCode();
        this.maxRepeat = maxRepeat;
        this.lastSent = lastSent;
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
       return SubscriptionDeliveryMethod.get(alertAlertChangeMethod);
    }

    @Override
    public void setAlertStateChangeMethod(SubscriptionDeliveryMethod alertStateChangeMethod) {
       this.alertAlertChangeMethod = alertStateChangeMethod.getCode();
    }

    @Override
    public SubscriptionDeliveryMethod getPropertyChangeMethod() {
      return  SubscriptionDeliveryMethod.get(this.propertyChangeMethod);
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
        subscribedPointUUID = uuid;
    }

    @Override
    public long getCategoryId() {
      return this.categoryId;
    }

    @Override
    public void setCategoryId(long categoryId) {
      this.categoryId = categoryId;
    }

    public double getMaxRepeat() {
        return maxRepeat;
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
}
