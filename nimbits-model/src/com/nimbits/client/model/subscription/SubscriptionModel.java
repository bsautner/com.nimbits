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
    private int dataNotifyMethod;
    private int alertNotifyMethod;
    private int changeNotifyMethod;
    private double maxRepeat;
    private Date lastSent;
    private boolean notifyFormatJson;
    private boolean enabled;
    public SubscriptionModel() {
    }

    public SubscriptionModel(Subscription subscription) {
        this.uuid = subscription.getUuid();
        this.subscribedEntity = subscription.getSubscribedEntity();
        this.dataNotifyMethod = subscription.getDataNotifyMethod().getCode();
        this.alertNotifyMethod = subscription.getAlertNotifyMethod().getCode();
        this.changeNotifyMethod = subscription.getChangeNotifyMethod().getCode();
        this.maxRepeat = subscription.getMaxRepeat();
        this.lastSent = subscription.getLastSent();
        this.notifyFormatJson = subscription.getNotifyFormatJson();
        this.enabled = subscription.getEnabled();
    }

    public SubscriptionModel(SubscriptionDeliveryMethod dataUpdateAlertMethod,
                             SubscriptionDeliveryMethod alertStateChangeMethod,
                             SubscriptionDeliveryMethod propertyChangeMethod,
                             double maxRepeat,
                             Date lastSent, boolean formatJson, boolean enabled) {

        this.dataNotifyMethod = dataUpdateAlertMethod.getCode();
        this.alertNotifyMethod = alertStateChangeMethod.getCode();
        this.changeNotifyMethod = propertyChangeMethod.getCode();
        this.maxRepeat = maxRepeat;
        this.lastSent = lastSent;
        this.enabled = enabled;
        this.notifyFormatJson = formatJson;
    }



    @Override
    public SubscriptionDeliveryMethod getDataNotifyMethod() {
      return SubscriptionDeliveryMethod.get(dataNotifyMethod);
    }

    @Override
    public void setDataNotifyMethod(SubscriptionDeliveryMethod dataNotifyMethod) {
      this.dataNotifyMethod = dataNotifyMethod.getCode();
    }

    @Override
    public SubscriptionDeliveryMethod getAlertNotifyMethod() {
       return SubscriptionDeliveryMethod.get(alertNotifyMethod);
    }

    @Override
    public void setAlertNotifyMethod(SubscriptionDeliveryMethod alertStateChangeMethod) {
       this.alertNotifyMethod = alertStateChangeMethod.getCode();
    }

    @Override
    public SubscriptionDeliveryMethod getChangeNotifyMethod() {
      return  SubscriptionDeliveryMethod.get(this.changeNotifyMethod);
    }

    @Override
    public void setChangeNotifyMethod(SubscriptionDeliveryMethod changeNotifyMethod) {
       this.changeNotifyMethod = changeNotifyMethod.getCode();
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
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public void setUuid(String uuid) {
        subscribedEntity = uuid;
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
