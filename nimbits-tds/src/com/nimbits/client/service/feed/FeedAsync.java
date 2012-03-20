package com.nimbits.client.service.feed;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.feed.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;
@SuppressWarnings("unused")
public interface FeedAsync {

    void getFeed(final int count, final AsyncCallback<List<FeedValue>> async);

    void postToFeed(final User user, final Entity entity, final Point originalPoint, final Value value, final FeedType type, AsyncCallback<Void> async);

    void postToFeed(final User user, final String html,  final FeedType type, final AsyncCallback<Void> async);

    void postToFeed(final User user, final NimbitsException ex, final AsyncCallback<Void> async);
}
