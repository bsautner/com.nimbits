package com.nimbits.client.service.feed;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.feed.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;
@SuppressWarnings("unused")
public interface FeedAsync {

    void getFeed(int count, AsyncCallback<List<FeedValue>> async);

    void postToFeed(User user, Entity entity, Point originalPoint, Value value, AsyncCallback<Void> async);

    void postToFeed(final User user, final String html, AsyncCallback<Void> async);

    void postToFeed(User user, NimbitsException ex, AsyncCallback<Void> async);
}
