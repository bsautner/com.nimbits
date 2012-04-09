package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:02 PM
 */
public class SubscriptionModel extends EntityModel implements Serializable, Subscription  {


    private String subscribedEntity;
    private int notifyMethod;
    private int subscriptionType;
    private double maxRepeat;
    private Date lastSent;
    private boolean notifyFormatJson;
    private boolean enabled;

    @SuppressWarnings("unused")
    private SubscriptionModel() {
    }

    public SubscriptionModel(Subscription subscription) throws NimbitsException {
        super(subscription);
        this.subscribedEntity = subscription.getSubscribedEntity();
        this.notifyMethod = subscription.getNotifyMethod().getCode();
        this.subscriptionType = subscription.getSubscriptionType().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();

    }

    public SubscriptionModel(
            Entity entity,
            String subscribedEntity,
                             SubscriptionType subscriptionType,
                             SubscriptionNotifyMethod subscriptionNotifyMethod,
                             double maxRepeat,
                             Date lastSent,
                             boolean formatJson,
                             boolean enabled) throws NimbitsException {
        super(entity);
        this.subscribedEntity = subscribedEntity;
        this.subscriptionType = subscriptionType.getCode();
        this.notifyMethod = subscriptionNotifyMethod.getCode();
        this.maxRepeat = maxRepeat;
        this.lastSent = new Date(lastSent.getTime());
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
    public double getMaxRepeat() {
        return maxRepeat;
    }

    @Override
    public void setMaxRepeat(double maxRepeat) {
        this.maxRepeat = maxRepeat;
    }

    @Override
    public Date getLastSent() {
        return lastSent;
    }

    @Override
    public void setLastSent(Date lastSent) {
        this.lastSent = new Date(lastSent.getTime());
    }


}
