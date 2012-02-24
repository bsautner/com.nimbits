package com.nimbits.client.service.feed;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:03 PM
 */
@RemoteServiceRelativePath("feed")
public interface Feed extends RemoteService{
    void postToFeed(User user, Value value);
    Point createFeedPoint(User user);
}
