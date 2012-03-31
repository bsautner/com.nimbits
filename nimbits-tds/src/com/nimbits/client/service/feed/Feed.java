/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:03 PM
 */
@RemoteServiceRelativePath("feed")
public interface Feed extends RemoteService{
    void postToFeed(final User user, final Entity entity, final Point originalPoint, final Value value,final FeedType type) throws NimbitsException;

    List<FeedValue> getFeed(final int count, final String feedOwnersUUID) throws NimbitsException;

    void postToFeed(final User user, final String html, final FeedType type) throws NimbitsException;

    void postToFeed(final User user, final NimbitsException ex);
}
