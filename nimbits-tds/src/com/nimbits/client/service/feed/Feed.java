package com.nimbits.client.service.feed;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.feed.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:03 PM
 */
@RemoteServiceRelativePath("feed")
public interface Feed extends RemoteService{
    void postToFeed(User user, Entity entity, Point originalPoint, Value value) throws NimbitsException;

    List<FeedValue> getFeed(int count) throws NimbitsException;

    void postToFeed(final User user, final String html) throws NimbitsException;
}
