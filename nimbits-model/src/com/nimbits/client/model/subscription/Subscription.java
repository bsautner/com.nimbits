package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:01 PM
 */
public interface Subscription extends Serializable {

    String getUUID();

    void setUUID(String uuid);

    SubscriptionDeliveryMethod getDataUpdateAlertMethod();

    void setDataUpdateAlertMethod(SubscriptionDeliveryMethod dataUpdateAlertMethod);

    SubscriptionDeliveryMethod getAlertStateChangeMethod();

    void setAlertStateChangeMethod(SubscriptionDeliveryMethod alertStateChangeMethod);

    SubscriptionDeliveryMethod getPropertyChangeMethod();

    void setPropertyChangeMethod(SubscriptionDeliveryMethod propertyChangeMethod);

    double getMaxRepeat();

    void setMaxRepeat(double maxRepeat);

    Date getLastSent();

    void setLastSent(Date lastSent);

    String getSubscribedEntityUUID();

    void setSubscribedEntityUUID(String uuid);
}
