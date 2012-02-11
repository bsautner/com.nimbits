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

package com.nimbits.client.service.datapoints;


import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.exceptions.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

@RemoteServiceRelativePath(Const.PARAM_POINT)
public interface PointService extends RemoteService {

    Point addPoint(final User user, final Entity entity);

    boolean checkPointProtection(final User loggedInUser, final User pointOwner, final Point p);

    Point addPoint(final EntityName pointName, final Category category) throws NimbitsException, PointExistsException;

    Point getPointByName(final User pointOwner, final EntityName name) throws NimbitsException;

    Point getPointByID(final User u, final long id) throws NimbitsException;

    List<Point> getPoints() throws NimbitsException;

    List<Point> getPointsByCategory(final Category category) throws NimbitsException;

    Point updatePoint(final Point point) throws NimbitsException;

    Point updatePoint(final User u, final Point point) throws NimbitsException;

    void deletePoint(final Point p) throws NimbitsException;

    void deletePoint(final User u, final Point p) throws NimbitsException;

    Map<EntityName, Point> getPointsByName(final long pointOwnerId, final Set<EntityName> names) throws NimbitsException;

    Point movePoint(final Point point, final EntityName newEntityName) throws NimbitsException;

    List<Point> getPoints(final User u) throws NimbitsException;

    Point getPointByUUID(final String uuid);

    AlertType getPointAlertState(final Point point, final Value value);

    Point copyPoint(final Point point, final EntityName newName) throws NimbitsException, PointExistsException;

    Point addPoint(final EntityName pointName) throws NimbitsException, PointExistsException;

    Point getPointByID(final long id) throws NimbitsException;


    String exportData(final Map<EntityName, Point> points, ExportType exportType) throws NimbitsException;

    Point movePoint(final User u, final Point point, final EntityName EntityName) throws NimbitsException;

    Point addPoint(final Point point, final Category c, final User u) throws NimbitsException;

    Point addPoint(final EntityName pointName, final Category c, final User u) throws NimbitsException;

    List<Point> getPointsByCategory(final User u, final Category c);

    List<Point> getAllPoints(int start, int end);

    List<Point> getIdlePoints();

    Point publishPoint(Point p) throws NimbitsException;

    Point publishPoint(User u, Point p) throws NimbitsException;

    List<Point> getAllPoints();

    Entity subscribe(Entity entity, Subscription subscription) throws NimbitsException;

    Subscription readSubscription(final Entity point) throws NimbitsException;

    void deleteSubscription(final Point point) throws NimbitsException;

    List<Subscription> getSubscriptionsToPoint(Point point);

    void updateSubscriptionLastSent(Subscription subscription);


    Entity copyPoint(User u, Entity originalEntity, EntityName newName);

    Map<String,Point> getPoints(Map<String, Entity> entities);
}
