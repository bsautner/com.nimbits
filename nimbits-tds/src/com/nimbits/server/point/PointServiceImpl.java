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
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.server.blob.BlobStoreFactory;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.export.ExportHelperFactory;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;

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
        newPoint.setName(newName);


        return addPoint(u, newPoint);

    }

    @Override
    public Map<String, Point> getPoints(Map<String, Entity> entities) throws NimbitsException {
        List<Entity> entityList = new ArrayList<Entity>(entities.values());

        List<Point> points =  PointTransactionsFactory.getInstance(getUser()).getPoints(entityList);
        Map<String, Point> retObj = new HashMap<String, Point>(points.size());
        Value v;
        for (Point p : points) {

            v  = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
            p.setValue(v);
            retObj.put(p.getKey(), p);
        }
        return retObj;

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


    @Override
    public Point addPoint(final User user, final Point point) throws NimbitsException {

        return PointTransactionsFactory.getInstance(user).addPoint(point);
    }

    @Override
    public Point addPoint(EntityName name) throws NimbitsException {
        User u = getUser();

        Entity r = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
               u.getKey(), u.getKey(), UUID.randomUUID().toString());
        return addPoint(u, r);


    }


    @Override
    public List<Point> getPoints(final User u, final List<Entity> entities) throws NimbitsException {
        return PointTransactionsFactory.getInstance(u).getPoints(entities);
    }


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


    @Override
    public List<Point> getIdlePoints() throws NimbitsException {
        return PointTransactionsFactory.getInstance(null).getIdlePoints();
    }
}
