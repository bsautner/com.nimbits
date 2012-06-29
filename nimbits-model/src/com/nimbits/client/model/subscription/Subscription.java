package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:01 PM
 */
public interface Subscription extends Entity, Serializable {

    double getMaxRepeat();

    void setMaxRepeat(double maxRepeat);

    Date getLastSent();

    void setLastSent(Date lastSent);

    String getSubscribedEntity();

    void setSubscribedEntity(String uuid);

    boolean getNotifyFormatJson();

    void setNotifyFormatJson(boolean notifyFormatJson);

    boolean getEnabled();

    void setEnabled(boolean enabled);

    SubscriptionNotifyMethod getNotifyMethod();

    void setNotifyMethod(SubscriptionNotifyMethod notifyMethod);

    SubscriptionType getSubscriptionType();

    void setSubscriptionType(SubscriptionType subscriptionType);

    @Override
    void update(Entity update) throws NimbitsException;
}
