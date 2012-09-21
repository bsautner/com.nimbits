package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;

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
    private int maxRepeat;
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
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();

    }

    public SubscriptionModel(
            Entity entity,
            String subscribedEntity,
                             SubscriptionType subscriptionType,
                             SubscriptionNotifyMethod subscriptionNotifyMethod,
                             int maxRepeat,
                             boolean formatJson,
                             boolean enabled) throws NimbitsException {
        super(entity);
        this.subscribedEntity = subscribedEntity;
        this.subscriptionType = subscriptionType.getCode();
        this.notifyMethod = subscriptionNotifyMethod.getCode();
        this.maxRepeat = maxRepeat;
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
    public int getMaxRepeat() {
        return maxRepeat;
    }

    @Override
    public void setMaxRepeat(int maxRepeat) {
        this.maxRepeat = maxRepeat;
    }


}
