package com.nimbits.client.service.subscription;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

public interface SubscriptionServiceAsync {
    void processSubscriptions(final Point point, final Value v, AsyncCallback<Void> async);

    void getSubscriptionsToPoint(Point point, AsyncCallback<List<Subscription>> async);

    void updateSubscriptionLastSent(Subscription subscription, AsyncCallback<Void> async);

    void subscribe(Entity entity, Subscription subscription, EntityName name, AsyncCallback<Entity> async);

    void readSubscription(final Entity point, AsyncCallback<Subscription> async);

    void getSubscribedEntity(final Entity entity, AsyncCallback<Entity> async);

    void deleteSubscription(User u, Entity entity, AsyncCallback<Void> async);
}
