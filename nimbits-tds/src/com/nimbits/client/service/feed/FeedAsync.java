package com.nimbits.client.service.feed;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

public interface FeedAsync {
    void postToFeed(User user, Value value, AsyncCallback<Void> async);

    void createFeedPoint(User user, AsyncCallback<Point> async);
}
