/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.cloudplatform.server.process.task;

import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.cloudplatform.server.admin.common.ServerInfo;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service("pointTask")

public class PointMaintTask extends HttpServlet  implements org.springframework.web.HttpRequestHandler{




    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

        try {
            processPost(req);

        } catch (Exception ex) {
            LogHelper.logException(this.getClass(), ex);
        }

    }


    public void processPost(final HttpServletRequest req) throws Exception {


        final String j = req.getParameter(Parameters.json.getText());
        final Point entity = GsonFactory.getInstance().fromJson(j, PointModel.class);
      //  final User u = UserServiceFactory.getInstance().getUserByKey(entity.getOwner(), AuthLevel.admin);
        if (entity.getExpire() > 0) {
            TaskImpl.startDeleteDataTask(
                    entity,
                    true, entity.getExpire());
        }
        consolidateBlobs(entity);
        TaskImpl.startCoreTask(null, entity, Action.update, ServerInfo.getFullServerURL(req));

    }



    public void consolidateBlobs(final Entity entity) throws Exception {
        final List<ValueBlobStore> stores = ValueTransaction.getAllStores(entity);
        if (! stores.isEmpty()) {
            final Collection<Long> dates = new ArrayList<Long>(stores.size());
            final Collection<Long> dupDates = new ArrayList<Long>(stores.size());
            for (final ValueBlobStore store : stores) {
                //consolidate blobs that have more than one date.

                if ( dates.contains(store.getTimestamp().getTime()) && ! dupDates.contains(store.getTimestamp().getTime())) {

                    dupDates.add(store.getTimestamp().getTime());
                }
                else {

                    dates.add(store.getTimestamp().getTime());
                }
            }
           // SystemServiceImpl.updateSystemPoint("Fragmented Dates Merges By Point Maint", dupDates.size(), false, PointType.backend);
            for (Long l : dupDates) {
                ValueTransaction.consolidateDate(entity, new Date(l));

            }
        }
    }



}

