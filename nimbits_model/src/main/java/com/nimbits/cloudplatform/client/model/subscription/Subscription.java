package com.nimbits.cloudplatform.client.model.subscription;

import com.nimbits.cloudplatform.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.cloudplatform.client.enums.subscription.SubscriptionType;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:01 PM
 */
public interface Subscription extends Entity, Serializable {

    int getMaxRepeat();

    void setMaxRepeat(int maxRepeat);

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
    void update(Entity update) ;
}
