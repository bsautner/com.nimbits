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
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.server.blobstore.BlobStoreFactory;
import com.nimbits.server.core.CoreFactory;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.export.ExportHelperFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;
import com.nimbits.server.subscription.SubscriptionTransactionFactory;
import com.nimbits.server.task.TaskFactoryLocator;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.user.UserTransactionFactory;

import java.util.*;

public class PointServiceImpl extends RemoteServiceServlet implements
        PointService {

    private static final long serialVersionUID = 1L;

    private User getUser() {
        User u;
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            u = null;
        }
        return u;
    }




    @Override
    public Entity copyPoint(User u, Entity originalEntity, EntityName newName) {


        final Point storedPoint = PointServiceFactory.getInstance().getPointByUUID(originalEntity.getUUID());
        final Point newPoint = PointModelFactory.createPointModel(storedPoint);
        final String newUUID = UUID.randomUUID().toString();
        newPoint.setName(newName);
        newPoint.setUuid(newUUID);

        final Entity newEntity = EntityModelFactory.createEntity(u, originalEntity);
        newEntity.setName(newName);
        newEntity.setUUID(newUUID);


        addPoint(u, newEntity, newPoint);



        return newEntity;
    }

    @Override
    public Map<String, Point> getPoints(Map<String, Entity> entities) {
        List<Entity> entityList = new ArrayList<Entity>(entities.values());

        List<Point> points =  PointTransactionsFactory.getInstance(getUser()).getPoints(entityList);
        Map<String, Point> retObj = new HashMap<String, Point>();

        for (Point p : points) {
            Value v  = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
            p.setValue(v);
            retObj.put(p.getUUID(), p);
        }
        return retObj;

    }



    public void deletePoint(final User u, final Point p) throws NimbitsException {

        PointTransactionsFactory.getInstance(u).deletePoint(p);
        String j = GsonFactory.getInstance().toJson(p);

        CoreFactory.getInstance().reportDeleteToCore(j, EntityType.point);

    }

//
//    @Override
//    public Point movePoint(User u, Point point, EntityName EntityName) throws NimbitsException {
//
//        return PointTransactionsFactory.getInstance(u).movePoint(point, EntityName);
//    }

//    @Override
//    public Point showEntityData(Point point, Category c, User u) throws NimbitsException {
//        Point result = PointTransactionsFactory.getInstance(u).showEntityData(point, c);
//        if (result != null) {
//            TaskFactoryLocator.getInstance().startPointMaintTask(result);
//        }
//        return result;
//
//    }

//    @Override
//    public Point showEntityData(final EntityName pointName, final Category c, final User u) throws NimbitsException {
//        Point result = PointTransactionsFactory.getInstance(u).showEntityData(pointName, c);
//        if (result != null) {
//            TaskFactoryLocator.getInstance().startPointMaintTask(result);
//        }
//        return result;
//    }
//
//    @Override
//    public List<Point> getPointsByCategory(final User u, final Category c) {
//        return PointTransactionsFactory.getInstance(u).getPointsByCategory(c);
//    }

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
    public Point getPointByName(final User pointOwner, final EntityName name) throws NimbitsException {
        final User u = pointOwner == null ? UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest()) : pointOwner;
        Entity entity = EntityTransactionFactory.getInstance(u).getEntityByName(name);
        return PointTransactionsFactory.getInstance(u).getPointByUUID(entity.getEntity());


    }

    @Deprecated
    @Override
    public Map<EntityName, Point> getPointsByName(final long pointOwnerId, final Set<EntityName> names) throws NimbitsException {

        final User loggedInUser = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        final User pointOwner = UserTransactionFactory.getInstance().getNimbitsUserByID(pointOwnerId);

        final Map<EntityName, Point> retObj = new HashMap<EntityName, Point>();
        for (final EntityName name : names) {
            final Point p = PointTransactionsFactory.getInstance(pointOwner).getPointByName(name);
            retObj.put(name, p);

        }
        return retObj;

    }

    @Override
        public Point addPoint(User user, Entity entity) {
        Entity r = EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
        return PointTransactionsFactory.getInstance(user).addPoint(r);
    }

    @Override
    public Point addPoint(User user, Entity entity, Point point) {
        Entity r = EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
        return PointTransactionsFactory.getInstance(user).addPoint(entity, point);
    }

    @Override
    public Point addPoint(EntityName name) {
        User u = getUser();

        Entity r = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                UUID.randomUUID().toString(), u.getUuid(), u.getUuid());
        return addPoint(u, r);


    }


//    @Override
//    public Point showEntityData(final EntityName pointName, final Category c) throws NimbitsException, PointExistsException {
//
//
//        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
//                this.getThreadLocalRequest());
//
//
//        Point result = PointTransactionsFactory.getInstance(u).showEntityData(pointName, c);
//        if (result != null) {
//            TaskFactoryLocator.getInstance().startPointMaintTask(result);
//        }
//        return result;
//
//
//    }

//    @Override
//    public Point showEntityData(final EntityName pointName) throws NimbitsException, PointExistsException {
//
//        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
//                this.getThreadLocalRequest());
//
//        Entity entity = EntityModelFactory.createEntity(pointName, EntityType.point);
//        Entity result = EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
//
//        if (result != null) {
//           // TaskFactoryLocator.getInstance().startPointMaintTask(result);
//        }
//        return result;
//
//    }

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
    public Point getPointByID(final User u, final long id) throws NimbitsException {
        return PointTransactionsFactory.getInstance(u).getPointByID(id);
    }


    @Override
    public String exportData(final Map<EntityName, Point> points, final ExportType exportType) throws NimbitsException {
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
    public Point getPointByUUID(final String uuid)  {
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
    public List<Point> getIdlePoints() {
        return PointTransactionsFactory.getInstance(null).getIdlePoints();
    }
}
