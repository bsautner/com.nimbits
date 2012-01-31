package com.nimbits.server.orm;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.subscription.*;

import javax.jdo.annotations.*;
import java.io.*;

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
    private Key key;

    @Persistent
    private String subscriberUUID;

    @Persistent
    private String subscribedPointUUID;

    @Persistent
    private Long categoryId;

    @Persistent
    private int dataUpdateAlertMethod = SubscriptionDeliveryMethod.none.getCode();

    @Persistent
    private int alarmStateChangeMethod = SubscriptionDeliveryMethod.none.getCode();

    @Persistent
    private int propertyChangeMethod = SubscriptionDeliveryMethod.none.getCode();

    public SubscriptionEntity(final String subscriberUUID) {
        this.subscriberUUID = subscriberUUID;
    }

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(Subscription subscription) {
        this.subscriberUUID = subscription.getSubscriberUUID();
        this.dataUpdateAlertMethod = subscription.getDataUpdateAlertMethod().getCode();
        this.alarmStateChangeMethod = subscription.getAlarmStateChangeMethod().getCode();
        this.propertyChangeMethod = subscription.getPropertyChangeMethod().getCode();
        this.subscribedPointUUID = subscription.getSubscribedPointUUID();
        this.categoryId = subscription.getCategoryId();
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

    public String getKey() {
        return key.toString();
    }
}
