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

    String getUuid();

    void setUuid(String uuid);

    SubscriptionDeliveryMethod getDataNotifyMethod();

    void setDataNotifyMethod(SubscriptionDeliveryMethod dataUpdateAlertMethod);

    SubscriptionDeliveryMethod getAlertNotifyMethod();

    void setAlertNotifyMethod(SubscriptionDeliveryMethod alertStateChangeMethod);

    SubscriptionDeliveryMethod getChangeNotifyMethod();

    void setChangeNotifyMethod(SubscriptionDeliveryMethod propertyChangeMethod);

    double getMaxRepeat();

    void setMaxRepeat(double maxRepeat);

    Date getLastSent();

    void setLastSent(Date lastSent);

    String getSubscribedEntity();

    void setSubscribedEntity(String uuid);

    public boolean getNotifyFormatJson();

    public void setNotifyFormatJson(boolean notifyFormatJson);

    public boolean getEnabled();

    public void setEnabled(boolean enabled);
}
