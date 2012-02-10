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
    private String uuid;
    private String subscribedEntityUUID;
    private int dataUpdateAlertMethod;
    private int alertAlertChangeMethod;
    private int propertyChangeMethod;
    private double maxRepeat;
    private Date lastSent;

    public SubscriptionModel() {
    }

    public SubscriptionModel(Subscription subscription) {
        this.uuid = subscription.getUUID();
        this.subscribedEntityUUID = subscription.getSubscribedEntityUUID();
        this.dataUpdateAlertMethod = subscription.getDataUpdateAlertMethod().getCode();
        this.alertAlertChangeMethod = subscription.getAlertStateChangeMethod().getCode();
        this.propertyChangeMethod = subscription.getPropertyChangeMethod().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();


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
    public String getSubscribedEntityUUID() {
     return this.subscribedEntityUUID;
    }

    @Override
    public void setSubscribedEntityUUID(String uuid) {
      this.subscribedEntityUUID = uuid;
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }

    @Override
    public void setUUID(String uuid) {
        subscribedEntityUUID = uuid;
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
