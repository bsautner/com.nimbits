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
    private String subscribedEntity;
    private int notifyMethod;
    private int subscriptionType;
    private double maxRepeat;
    private Date lastSent;
    private boolean notifyFormatJson;
    private boolean enabled;
    public SubscriptionModel() {
    }

    public SubscriptionModel(Subscription subscription) {
        this.uuid = subscription.getKey();
        this.subscribedEntity = subscription.getSubscribedEntity();
        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
    }

    public SubscriptionModel(String subscribedEntity,
                             SubscriptionType subscriptionType,
                             SubscriptionNotifyMethod subscriptionNotifyMethod,
                             double maxRepeat,
                             Date lastSent,
                             boolean formatJson,
                             boolean enabled) {
        this.subscribedEntity = subscribedEntity;
        this.subscriptionType = subscriptionType.getCode();
        this.notifyMethod = subscriptionNotifyMethod.getCode();
        this.maxRepeat = maxRepeat;
        this.lastSent = lastSent;
        this.enabled = enabled;
        this.notifyFormatJson = formatJson;
    }



    @Override
    public String getSubscribedEntity() {
     return this.subscribedEntity;
    }

    @Override
    public void setSubscribedEntity(String uuid) {
      this.subscribedEntity = uuid;
    }

    @Override
    public boolean getNotifyFormatJson() {
        return this.notifyFormatJson;
    }

    @Override
    public void setNotifyFormatJson(boolean notifyFormatJson) {
        this.notifyFormatJson = notifyFormatJson;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
       this.enabled = enabled;
    }
    @Override
    public SubscriptionNotifyMethod getNotifyMethod() {
        return SubscriptionNotifyMethod.get(this.notifyMethod);
    }
    @Override
    public void setNotifyMethod(SubscriptionNotifyMethod notifyMethod) {
        this.notifyMethod = notifyMethod.getCode();
    }
    @Override
    public SubscriptionType getSubscriptionType() {
        return SubscriptionType.get(subscriptionType);
    }
    @Override
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType.getCode();
    }
    @Override
    public String getKey() {
        return this.uuid;
    }

    @Override
    public void setUuid(String uuid) {
       this.uuid = uuid;
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
