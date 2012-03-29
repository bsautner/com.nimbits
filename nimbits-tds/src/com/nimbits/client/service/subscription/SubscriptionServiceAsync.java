package com.nimbits.client.service.subscription;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.List;

public interface SubscriptionServiceAsync {
    void processSubscriptions(final Point point, final Value v, AsyncCallback<Void> async);

    void getSubscriptionsToPoint(Point point, AsyncCallback<List<Subscription>> async);

    void updateSubscriptionLastSent(Subscription subscription, AsyncCallback<Void> async);

    void subscribe(Entity entity, Subscription subscription, EntityName name, AsyncCallback<Entity> async);

    void readSubscription(final Entity point, AsyncCallback<Subscription> async);

    void getSubscribedEntity(final Entity entity, AsyncCallback<Entity> async);

    void deleteSubscription(User u, Entity entity, AsyncCallback<Void> async);
}
