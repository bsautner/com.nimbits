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

package com.nimbits.server.point;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.PointExistsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.server.blobstore.BlobStoreFactory;
import com.nimbits.server.core.CoreFactory;
import com.nimbits.server.export.ExportHelperFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.pointcategory.CategoryServiceFactory;
import com.nimbits.server.subscription.*;
import com.nimbits.server.task.TaskFactoryLocator;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.user.UserTransactionFactory;

import java.util.*;

public class PointServiceImpl extends RemoteServiceServlet implements
        PointService {


    private static final long serialVersionUID = 1L;


    //called from rpc
    public void deletePoint(final Point p) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        if (p.getEntityType().equals(EntityType.point)) {
            deletePoint(u, p);
        }
        else if (p.getEntityType().equals(EntityType.subscription)) {
           deleteSubscription(p);
        }

    }


    public void deletePoint(final User u, final Point p) throws NimbitsException {

        PointTransactionsFactory.getInstance(u).deletePoint(p);
        String j = GsonFactory.getInstance().toJson(p);

        CoreFactory.getInstance().reportDeleteToCore(j, EntityType.point);

    }


    @Override
    public Point movePoint(User u, Point point, CategoryName categoryName) throws NimbitsException {

        return PointTransactionsFactory.getInstance(u).movePoint(point, categoryName);
    }

    @Override
    public Point addPoint(Point point, Category c, User u) throws NimbitsException {
        Point result = PointTransactionsFactory.getInstance(u).addPoint(point, c);
        if (result != null) {
            TaskFactoryLocator.getInstance().startPointMaintTask(result);
        }
        return result;

    }

    @Override
    public Point addPoint(final PointName pointName, final Category c, final User u) throws NimbitsException {
        Point result = PointTransactionsFactory.getInstance(u).addPoint(pointName, c);
        if (result != null) {
            TaskFactoryLocator.getInstance().startPointMaintTask(result);
        }
        return result;
    }

    @Override
    public List<Point> getPointsByCategory(final User u, final Category c) {
        return PointTransactionsFactory.getInstance(u).getPointsByCategory(c);
    }

//    @Override
//    public Point checkPoint(HttpServletRequest req, EmailAddress email, Point point) {
//      return PointTransactionsFactory.getInstance().checkPoint(req, email, point);
//    }


    @Override
    public Point publishPoint(final Point p) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        return publishPoint(u, p);
    }

    @Override
    public Point publishPoint(final User u, final Point p) throws NimbitsException {

        Point result = PointTransactionsFactory.getInstance(u).publishPoint(p);
        if (result != null) {
            TaskFactoryLocator.getInstance().startPointMaintTask(result);
        }
        return result;
    }

    @Override
    public Point updatePoint(final Point point) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        return updatePoint(u, point);
    }

    @Override
    public Point updatePoint(final User u, final Point point) throws NimbitsException {

        Point result = PointTransactionsFactory.getInstance(u).updatePoint(point);
        if (result != null) {
            TaskFactoryLocator.getInstance().startPointMaintTask(result);
        }
        return result;
    }


    public AlertType getPointAlertState(final Point point, final Value value)  {
        AlertType retObj = AlertType.OK;

        if (point.isHighAlarmOn() || point.isLowAlarmOn()) {

            if (point.isHighAlarmOn() && (value.getNumberValue() >= point.getHighAlarm())) {
                retObj = AlertType.HighAlert;
            }
            if (point.isLowAlarmOn() && value.getNumberValue() <= point.getLowAlarm()) {
                retObj = AlertType.LowAlert;
            }

        }
        if (point.isIdleAlarmOn()) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, point.getIdleSeconds() * -1);

            if (point.getIdleSeconds() > 0 && value != null &&
                    value.getTimestamp().getTime() <= c.getTimeInMillis()) {

                retObj = AlertType.IdleAlert;
            }

        }
        return retObj;

    }

    @Override
    public Point getPointByName(final User pointOwner, final PointName name) throws NimbitsException {
        final User u = pointOwner == null ? UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest()) : pointOwner;

        return PointTransactionsFactory.getInstance(u).getPointByName(name);

    }

    @Override
    public Map<PointName, Point> getPointsByName(final long pointOwnerId, final Set<PointName> names) throws NimbitsException {

        final User loggedInUser = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        final User pointOwner = UserTransactionFactory.getInstance().getNimbitsUserByID(pointOwnerId);

        final Map<PointName, Point> retObj = new HashMap<PointName, Point>();
        for (final PointName name : names) {
            final Point p = PointTransactionsFactory.getInstance(pointOwner).getPointByName(name);
            if (p != null && checkPointProtection(loggedInUser, pointOwner, p)) {
                retObj.put(name, p);
            }
        }
        return retObj;

    }

    public boolean checkPointProtection(final User loggedInUser, final User pointOwner, final Point p) {
        long loggedInUserId = 0;

        if (loggedInUser != null) {
            loggedInUserId = loggedInUser.getId();
        }
        return loggedInUserId == pointOwner.getId() || p.isPublic() || pointOwner.getConnections().contains(loggedInUserId);
    }

    @Override
    public Point movePoint(final Point point, final CategoryName newCategoryName) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        if (point.getEntityType().equals(EntityType.point)) {
            return PointTransactionsFactory.getInstance(u).movePoint(point, newCategoryName);
        }
        else if (point.getEntityType().equals(EntityType.subscription)) {
            return SubscriptionTransactionFactory.getInstance(u).moveSubscription(point, newCategoryName);
        }
        else {
            return null;
        }


    }

    @Override
    public Point addPoint(final PointName pointName, final Category c) throws NimbitsException, PointExistsException {


        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());


        Point result = PointTransactionsFactory.getInstance(u).addPoint(pointName, c);
        if (result != null) {
            TaskFactoryLocator.getInstance().startPointMaintTask(result);
        }
        return result;


    }

    @Override
    public Point addPoint(final PointName pointName) throws NimbitsException, PointExistsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        final CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(Const.CONST_HIDDEN_CATEGORY);

        Category targetCategory = CategoryServiceFactory.getInstance().getCategory(u, categoryName);
        if (targetCategory == null) {
            targetCategory = CategoryServiceFactory.getInstance().createHiddenCategory(u);
        }

        Point result = PointTransactionsFactory.getInstance(u).addPoint(pointName, targetCategory);
        if (result != null) {
            TaskFactoryLocator.getInstance().startPointMaintTask(result);
        }
        return result;

    }

    @Override
    public Point getPointByID(final long id) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        return getPointByID(u, id);
    }


    public List<Point> getPoints() throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        return getPoints(u);

    }
    //End RPC Calls

    public List<Point> getPoints(final User u) throws NimbitsException {
        return PointTransactionsFactory.getInstance(u).getPoints();
    }


    @Override
    public Point copyPoint(final Point point, final PointName newName) throws NimbitsException, PointExistsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        final Point storedPoint = PointServiceFactory.getInstance().getPointByID(u, point.getId());
        final Point newPoint = PointModelFactory.createPointModel(storedPoint);
        final Category category = CategoryServiceFactory.getInstance().getCategory(u, point.getCatID());
        newPoint.setName(newName);
        Point result = PointTransactionsFactory.getInstance(u).addPoint(newPoint, category);
        if (result != null) {
            TaskFactoryLocator.getInstance().startPointMaintTask(result);
        }
        return result;
    }

    //RPC
    @Override
    public List<Point> getPointsByCategory(final Category c) throws NimbitsException {
        List<Point> retObj = null;

        User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        if (u != null) {
            if (c.getUserFK() != u.getId()) {
                u = UserTransactionFactory.getInstance().getNimbitsUser(u.getEmail());
            }
            retObj = PointTransactionsFactory.getInstance(u).getPointsByCategory(c);
        }
        return retObj;
    }


    @Override
    public Point getPointByID(final User u, final long id) throws NimbitsException {
        return PointTransactionsFactory.getInstance(u).getPointByID(id);
    }


    @Override
    public String exportData(final Map<PointName, Point> points, final ExportType exportType) throws NimbitsException {
        final String data;

        switch (exportType) {
            case csvSeparateColumns:
                data = ExportHelperFactory.getInstance().exportPointDataToCSVSeparateColumns(points);
                return BlobStoreFactory.getInstance().createFile(data, exportType);

            case descriptiveStatistics:
                data = ExportHelperFactory.getInstance().exportPointDataToDescriptiveStatistics(points);
                return BlobStoreFactory.getInstance().createFile(data, exportType);

            case possibleContinuation:
                data = ExportHelperFactory.getInstance().exportPointDataToPossibleContinuation(points);
                return BlobStoreFactory.getInstance().createFile(data, exportType);


            default:
                return "";

        }


    }

    @Override
    public Point getPointByUUID(final String uuid) throws NimbitsException {
        return PointTransactionsFactory.getInstance(null).getPointByUUID(uuid);
    }

    @Override
    public List<Point> getAllPoints(int start, int end) {
        return PointTransactionsFactory.getInstance(null).getAllPoints(start, end);
    }

    @Override
    public List<Point> getAllPoints() {
        return PointTransactionsFactory.getInstance(null).getAllPoints();
    }

    @Override
    public Subscription subscribe(Point p, Subscription subscription) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        subscription.setSubscriberUUID(u.getUuid());
        subscription.setSubscribedPointUUID(p.getUUID());
        CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(Const.CONST_HIDDEN_CATEGORY);
        Category c = CategoryServiceFactory.getInstance().getCategoryByName(u, categoryName, false, false);
        subscription.setCategoryId(c.getId());
        subscription.setLastSent(new Date());

        return SubscriptionTransactionFactory.getInstance(u).subscribe(subscription);


    }

    @Override
    public Subscription readSubscription(Point point) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        return SubscriptionTransactionFactory.getInstance(u).readSubscription(point);
    }

    @Override
    public void deleteSubscription(Point point) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        SubscriptionTransactionFactory.getInstance(u).deleteSubscription(point);
    }

    @Override
    public List<Subscription> getSubscriptionsToPoint(Point point) {
       return SubscriptionTransactionFactory.getInstance(null).getSubscriptionsToPoint(point);
    }

    @Override
    public void updateSubscriptionLastSent(Subscription subscription) {
       SubscriptionTransactionFactory.getInstance(null).updateSubscriptionLastSent(subscription);
    }

    @Override
    public List<Point> getIdlePoints() {
        return PointTransactionsFactory.getInstance(null).getIdlePoints();
    }
}
