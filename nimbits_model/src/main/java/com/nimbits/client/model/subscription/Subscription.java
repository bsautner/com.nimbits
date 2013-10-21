/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.subscription;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.entity.Entity;

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
