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

package com.nimbits.server.transactions.service.point;


import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.server.io.blob.*;
import com.nimbits.server.io.export.*;

import java.util.*;

public class PointServiceImpl extends RemoteServiceServlet implements
        PointService {

    private static final long serialVersionUID = 1L;

//    //
//    @Override
//    public Entity copyPoint(final User u, final Entity originalEntity, final EntityName newName) throws NimbitsException {
//
//        //final Point storedPoint = PointServiceFactory.getInstance().getPointByKey(originalEntity.getKey());
//        final Point storedPoint = (Point) EntityTransactionFactory.getDaoInstance(u).getEntityByKey(originalEntity.getKey(), PointEntity.class).get(0);
//
//        final Point newPoint = PointModelFactory.createPointModel(storedPoint);
//        newPoint.setName(newName);
//
//        return EntityServiceFactory.getInstance().addUpdateEntity(u, newPoint);
//      //  return addPoint(u, newPoint);
//
//    }


    @Override
    public String exportData(final Map<EntityName, Entity> points, final ExportType exportType, final Map<EntityName, List<Value>> values) throws NimbitsException {

        final String data;

        switch (exportType) {
            case csvSeparateColumns:
                data = ExportHelperFactory.getInstance().exportPointDataToCSVSeparateColumns(points, values);
                final Entity entity = points.values().iterator().next();
                final EntityName name;
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


}
