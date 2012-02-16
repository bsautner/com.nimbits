package com.nimbits.client.service.subscription;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.value.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/15/12
 * Time: 3:52 PM
 */
@RemoteServiceRelativePath("subscribe")
public interface SubscriptionService  extends RemoteService {
    void processSubscriptions(final Point point, final Value v);
    void deleteSubscription(final Point point) throws NimbitsException;
    List<Subscription> getSubscriptionsToPoint(Point point);
    void updateSubscriptionLastSent(Subscription subscription);
    Entity subscribe(Entity entity, Subscription subscription, EntityName name);
    Subscription readSubscription(final Entity point) throws NimbitsException;
    Entity getSubscribedEntity(final Entity entity);

}
