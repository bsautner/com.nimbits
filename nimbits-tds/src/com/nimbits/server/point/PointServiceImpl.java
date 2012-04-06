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


import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.server.blob.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.export.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.task.*;
import com.nimbits.server.user.*;
import com.nimbits.server.value.*;

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
//
    @Override
    public Entity copyPoint(User u, Entity originalEntity, EntityName newName) throws NimbitsException {

        //final Point storedPoint = PointServiceFactory.getInstance().getPointByKey(originalEntity.getKey());
        Point storedPoint = (Point) EntityTransactionFactory.getDaoInstance(u).getEntityByKey(originalEntity.getKey(), PointEntity.class);

        final Point newPoint = PointModelFactory.createPointModel(storedPoint);


        final Entity newEntity = EntityModelFactory.createEntity(u, originalEntity);
        newEntity.setName(newName);

        addPoint(u, newEntity, newPoint);
        return newEntity;
    }

    @Override
    public Map<String, Point> getPoints(Map<String, Entity> entities) throws NimbitsException {
        List<Entity> entityList = new ArrayList<Entity>(entities.values());

        List<Entity> points =  PointTransactionsFactory.getInstance(getUser()).getPoints(entityList);
        Map<String, Point> retObj = new HashMap<String, Point>(points.size());
        Value v;
        for (Entity p : points) {
            Point px = (Point) p;
            v  = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
            px.setValue(v);
            retObj.put(p.getKey(), px);
        }
        return retObj;

    }

    @Override
    public void deletePoint(User u, Entity entity) throws NimbitsException {
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
        //final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
        final Point newPoint =  PointTransactionsFactory.getInstance(user).addPoint(entity);
      //  notifyFeedOfNewPoint(user, entity);
        return newPoint;
    }

    private static void notifyFeedOfNewPoint(final User user, final Entity entity) throws NimbitsException {
        FeedServiceFactory.getInstance().postToFeed(user, "A new data point named " + entity.getName().getValue() +
        " has been created.", FeedType.info);
    }

    @Override
    public Point addPoint(final User user, final Entity entity, final Point point) throws NimbitsException {

        return PointTransactionsFactory.getInstance(user).addPoint(entity, point);
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
    public List<Entity> getPoints(final User u, final List<Entity> entities) throws NimbitsException {
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
    public String exportData(final Map<EntityName, Entity> points, final ExportType exportType, final Map<EntityName, List<Value>> values) throws NimbitsException {

        final String data;

        switch (exportType) {
            case csvSeparateColumns:
                data = ExportHelperFactory.getInstance().exportPointDataToCSVSeparateColumns(points, values);
                Entity entity = points.values().iterator().next();
                EntityName name;
                name = entity != null ? entity.getName() : CommonFactoryLocator.getInstance().createName("nimbits_export", EntityType.file);
                return BlobStoreFactory.getInstance().createFile(name, data, exportType);

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

//    @Override
//    public Point getPointByKey(final String uuid) throws NimbitsException {
//        return PointTransactionsFactory.getInstance(null).getPointByKey(uuid);
//    }

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
    public List<Entity> getIdlePoints() throws NimbitsException {
        return PointTransactionsFactory.getInstance(null).getIdlePoints();
    }
}
