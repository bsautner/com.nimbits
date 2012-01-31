package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:01 PM
 */
public interface Subscription extends Serializable {

    String getSubscriberUUID();

    void setSubscriberUUID(String subscriberUUID);

    SubscriptionDeliveryMethod getDataUpdateAlertMethod();

    void setDataUpdateAlertMethod(SubscriptionDeliveryMethod dataUpdateAlertMethod);

    SubscriptionDeliveryMethod getAlarmStateChangeMethod();

    void setAlarmStateChangeMethod(SubscriptionDeliveryMethod alarmStateChangeMethod);

    SubscriptionDeliveryMethod getPropertyChangeMethod();

    void setPropertyChangeMethod(SubscriptionDeliveryMethod propertyChangeMethod);

    String getSubscribedPointUUID();

    void setSubscribedPointUUID(String uuid);

    long getCategoryId();

    void setCategoryId(long categoryId);

}
