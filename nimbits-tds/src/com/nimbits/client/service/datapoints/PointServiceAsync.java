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
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

public interface PointServiceAsync {


    void addPoint(final PointName pointName, final Category category, final AsyncCallback<Point> async);

    void addPoint(final PointName pointName, final AsyncCallback<Point> async);

    void getPointsByCategory(final Category category,
                             final AsyncCallback<List<Point>> asyncCallback);

    void getPoints(final AsyncCallback<List<Point>> asyncCallback);

    void updatePoint(final Point point, final AsyncCallback<Point> asyncCallback);

    void getPointByName(final User pointOwner, final PointName name,
                        final AsyncCallback<Point> asyncCallback);

    void getPointByID(final long id, final AsyncCallback<Point> asyncCallback);

    void deletePoint(final Point p, final AsyncCallback<Void> async);

    //public void getPointCount(AsyncCallback<Integer> async);

    void movePoint(final PointName pointName, final CategoryName targetCategoryName,
                   final AsyncCallback<Point> async);

    void getPoints(final User u, final AsyncCallback<List<Point>> callback);

    void getPointByUUID(final String uuid, final AsyncCallback<Point> asyncCallback);

    void getPointsByName(final long pointOwnerId, final Set<PointName> names, final AsyncCallback<Map<PointName, Point>> async);

    void checkPointProtection(final User loggedInUser, final User pointOwner, final Point p, final AsyncCallback<Boolean> async);

    // void getPointAlertState(final Point point, final AsyncCallback<AlertType> async);

    void getPointAlertState(final Point point, final Value value, final AsyncCallback<AlertType> async);

    void copyPoint(final Point point, final PointName newName, final AsyncCallback<Point> async);

    void getPointByID(final User u, final long id, final AsyncCallback<Point> async);

    void exportData(final Map<PointName, Point> points, ExportType exportType, AsyncCallback<String> async);

    void getAllPoints(final int start, final int end, AsyncCallback<List<Point>> async);

    void getIdlePoints(final AsyncCallback<List<Point>> async);

    void addPoint(final PointName pointName, final Category c, final User u, AsyncCallback<Point> async);

    void addPoint(final Point point, final Category c, final User u, AsyncCallback<Point> async);

    void movePoint(final User u, final PointName pointName, final CategoryName categoryName, AsyncCallback<Point> async);

    void publishPoint(Point p, AsyncCallback<Point> asyncCallback);

    void updatePoint(final User u, final Point point, AsyncCallback<Point> async);

    void deletePoint(final User u, final Point p, AsyncCallback<Void> async);

    void getPointsByCategory(final User u, final Category c, AsyncCallback<List<Point>> async);

    void publishPoint(final User u, final Point p, final AsyncCallback<Point> async);

    void getAllPoints(final AsyncCallback<List<Point>> async);

    void subscribe(Point p, Subscription subscription, AsyncCallback<Subscription> async);

    void readSubscription(final Point point, AsyncCallback<Subscription> async);
}
