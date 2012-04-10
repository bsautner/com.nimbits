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
import com.nimbits.server.orm.*;
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
        newPoint.setName(newName);

        return EntityServiceFactory.getInstance().addUpdateEntity(u, newPoint);
      //  return addPoint(u, newPoint);

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
    public Point addPoint(EntityName name) throws NimbitsException {
        Entity e = EntityModelFactory.createEntity(name, EntityType.point);
        return (Point) EntityServiceFactory.getInstance().addUpdateEntity(e);
    }

    @Override
    public void addPoint(User user, Point point) throws NimbitsException {
        EntityServiceFactory.getInstance().addUpdateEntity(user, point);
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
        return PointTransactionsFactory.getInstance(UserServiceFactory.getServerInstance().getAdmin()).getIdlePoints();
    }
}
