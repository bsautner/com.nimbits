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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.datapoints;


import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.exceptions.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

@RemoteServiceRelativePath(Const.PARAM_POINT)
public interface PointService extends RemoteService {

    boolean checkPointProtection(final User loggedInUser, final User pointOwner, final Point p);

    Point addPoint(final PointName pointName, final Category category) throws NimbitsException, PointExistsException;

    Point getPointByName(final User pointOwner, final PointName name) throws NimbitsException;

    Point getPointByID(final User u, final long id) throws NimbitsException;

    List<Point> getPoints() throws NimbitsException;

    List<Point> getPointsByCategory(final Category category) throws NimbitsException;

    Point updatePoint(final Point point) throws NimbitsException;

    Point updatePoint(final User u, final Point point) throws NimbitsException;

    void deletePoint(final Point p) throws NimbitsException;

    void deletePoint(final User u, final Point p) throws NimbitsException;

    Map<PointName, Point> getPointsByName(final long pointOwnerId, final Set<PointName> names) throws NimbitsException;

    Point movePoint(final PointName pointName, final CategoryName newCategoryName) throws NimbitsException;

    List<Point> getPoints(final User u) throws NimbitsException;

    Point getPointByUUID(final String uuid) throws NimbitsException;

    AlertType getPointAlertState(final Point point, final Value value) throws NimbitsException;

    Point copyPoint(final Point point, final PointName newName) throws NimbitsException, PointExistsException;

    Point addPoint(final PointName pointName) throws NimbitsException, PointExistsException;

    Point getPointByID(final long id) throws NimbitsException;


    String exportData(final Map<PointName, Point> points, ExportType exportType) throws NimbitsException;

    Point movePoint(final User u, final PointName pointName, final CategoryName categoryName) throws NimbitsException;

    Point addPoint(final Point point, final Category c, final User u) throws NimbitsException;

    Point addPoint(final PointName pointName, final Category c, final User u) throws NimbitsException;

    List<Point> getPointsByCategory(final User u, final Category c);

    List<Point> getAllPoints(int start, int end);

    List<Point> getIdlePoints();

    Point publishPoint(Point p) throws NimbitsException;

    Point publishPoint(User u, Point p) throws NimbitsException;

    List<Point> getAllPoints();

    Subscription subscribe(Point p, Subscription subscription) throws NimbitsException;

    Subscription readSubscription(final Point point) throws NimbitsException;
}
