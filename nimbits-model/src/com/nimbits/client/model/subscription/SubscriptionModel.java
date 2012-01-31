package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.*;

import java.io.*;

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
    private int alarmStateChangeMethod;
    private int propertyChangeMethod;
    private long categoryId;

    public SubscriptionModel() {
    }

    public SubscriptionModel(Subscription subscription) {
        this.subscriberUUID = subscription.getSubscriberUUID();
        this.subscribedPointUUID = subscription.getSubscribedPointUUID();
        this.dataUpdateAlertMethod = subscription.getDataUpdateAlertMethod().getCode();
        this.alarmStateChangeMethod = subscription.getAlarmStateChangeMethod().getCode();
        this.propertyChangeMethod = subscription.getPropertyChangeMethod().getCode();

    }

    public SubscriptionModel(SubscriptionDeliveryMethod dataUpdateAlertMethod,
                             SubscriptionDeliveryMethod alarmStateChangeMethod,
                             SubscriptionDeliveryMethod propertyChangeMethod) {

        this.dataUpdateAlertMethod = dataUpdateAlertMethod.getCode();
        this.alarmStateChangeMethod = alarmStateChangeMethod.getCode();
        this.propertyChangeMethod = propertyChangeMethod.getCode();
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
    public SubscriptionDeliveryMethod getAlarmStateChangeMethod() {
       return SubscriptionDeliveryMethod.get(alarmStateChangeMethod);
    }

    @Override
    public void setAlarmStateChangeMethod(SubscriptionDeliveryMethod alarmStateChangeMethod) {
       this.alarmStateChangeMethod = alarmStateChangeMethod.getCode();
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

}
