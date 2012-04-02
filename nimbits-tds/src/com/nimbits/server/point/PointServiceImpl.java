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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.FeedType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.server.blob.BlobStoreFactory;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.export.ExportHelperFactory;
import com.nimbits.server.feed.FeedServiceFactory;
import com.nimbits.server.task.TaskFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//
    @Override
    public Entity copyPoint(User u, Entity originalEntity, EntityName newName) throws NimbitsException {
        final Point storedPoint = PointServiceFactory.getInstance().getPointByKey(originalEntity.getKey());
        final Point newPoint = PointModelFactory.createPointModel(storedPoint);


        final Entity newEntity = EntityModelFactory.createEntity(u, originalEntity);
        newEntity.setName(newName);

        addPoint(u, newEntity, newPoint);
        return newEntity;
    }

    @Override
    public Map<String, Point> getPoints(Map<String, Entity> entities) throws NimbitsException {
        List<Entity> entityList = new ArrayList<Entity>(entities.values());

        List<Point> points =  PointTransactionsFactory.getInstance(getUser()).getPoints(entityList);
        Map<String, Point> retObj = new HashMap<String, Point>();
        Value v;
        for (Point p : points) {
            v  = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
            p.setValue(v);
            retObj.put(p.getKey(), p);
        }
        return retObj;

    }

    @Override
    public void deletePoint(User u, Entity entity) {
          Point deleted = PointTransactionsFactory.getInstance(u).deletePoint(entity);
          TaskFactory.getInstance().startDeleteDataTask(deleted, false, 0);
    }

    @Override
    public Point updatePoint(final Point point) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        return updatePoint(u, point);
    }

    @Override
    public Point updatePoint(final User u, final Point point) throws NimbitsException {

        return PointTransactionsFactory.getInstance(u).updatePoint(point);
    }


    @Override
        public Point addPoint(final User user,final Entity entity) throws NimbitsException {
        final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
        final Point newPoint =  PointTransactionsFactory.getInstance(user).addPoint(r);
        notifyFeedOfNewPoint(user, entity);
        return newPoint;
    }

    private static void notifyFeedOfNewPoint(final User user, final Entity entity) throws NimbitsException {
        FeedServiceFactory.getInstance().postToFeed(user, "A new data point named " + entity.getName().getValue() +
        " has been created.", FeedType.info);
    }

    @Override
    public Point addPoint(final User user, final Entity entity, final Point point) throws NimbitsException {
        Entity result =  EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
        notifyFeedOfNewPoint(user, entity);
        return PointTransactionsFactory.getInstance(user).addPoint(result, point);
    }

    @Override
    public Point addPoint(EntityName name) throws NimbitsException {
        User u = getUser();

        Entity r = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
               u.getKey(), u.getKey());
        return addPoint(u, r);


    }

//    @Override
//    public Point getPointByID(final long id) throws NimbitsException {
//        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
//                this.getThreadLocalRequest());
//        return getPointByID(u, id);
//    }

//
//    public List<Point> getPoints() throws NimbitsException {
//
//        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
//                this.getThreadLocalRequest());
//        return getPoints(u);
//
//    }

    @Override
    public List<Point> getPoints(final User u, final List<Entity> entities) {
        return PointTransactionsFactory.getInstance(u).getPoints(entities);
    }
//    //End RPC Calls
//
//    public List<Point> getPoints(final User u) throws NimbitsException {
//        return PointTransactionsFactory.getInstance(u).getPoints();
//    }


//    @Override
//    public Point getPointByID(final User u, final long id) throws NimbitsException {
//        return PointTransactionsFactory.getInstance(u).getPointByID(id);
//    }


    @Override
    public String exportData(final Map<EntityName, Entity> points, final ExportType exportType, final Map<EntityName, List<Value>> values)  {

        final String data;

        switch (exportType) {
            case csvSeparateColumns:
                data = ExportHelperFactory.getInstance().exportPointDataToCSVSeparateColumns(points, values);
                return BlobStoreFactory.getInstance().createFile(data, exportType);

//            case descriptiveStatistics:
//                data = ExportHelperFactory.getInstance().exportPointDataToDescriptiveStatistics(points);
//                return BlobStoreFactory.getInstance().createFile(data, exportType);
//
//            case possibleContinuation:
//                data = ExportHelperFactory.getInstance().exportPointDataToPossibleContinuation(points);
//                return BlobStoreFactory.getInstance().createFile(data, exportType);


            default:
                return "";

        }


    }

    @Override
    public Point getPointByKey(final String uuid)  {
        return PointTransactionsFactory.getInstance(null).getPointByKey(uuid);
    }

//    @Override
//    public List<Point> getAllPoints(int start, int end) {
//        return PointTransactionsFactory.getInstance(null).getAllPoints(start, end);
//    }
//
//    @Override
//    public List<Point> getAllPoints() {
//        return PointTransactionsFactory.getInstance(null).getAllPoints();
//    }
//
//
    @Override
    public List<Point> getIdlePoints() {
        return PointTransactionsFactory.getInstance(null).getIdlePoints();
    }
}
