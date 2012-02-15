package com.nimbits.client.service.subscription;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.value.*;

import java.util.*;

public interface SubscriptionServiceAsync {
    void processSubscriptions(final Point point, final Value v, AsyncCallback<Void> async);

    void deleteSubscription(final Point point, AsyncCallback<Void> async);

    void getSubscriptionsToPoint(Point point, AsyncCallback<List<Subscription>> async);

    void updateSubscriptionLastSent(Subscription subscription, AsyncCallback<Void> async);
}
