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

package com.nimbits.server.process.task;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.admin.common.ServerInfoImpl;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.admin.system.SystemServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transactions.service.value.ValueTransactionFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class PointMaintTask extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointMaintTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        try {
            processPost(req);

        } catch (Exception ex) {
            LogHelper.logException(this.getClass(), ex);
        }

    }


    protected static void processPost(final HttpServletRequest req) throws NimbitsException {


        final String j = req.getParameter(Parameters.json.getText());
        final Point entity = GsonFactory.getInstance().fromJson(j, PointModel.class);
      //  final User u = UserServiceFactory.getInstance().getUserByKey(entity.getOwner(), AuthLevel.admin);
        if (entity.getExpire() > 0) {
            TaskFactory.getInstance().startDeleteDataTask(
                    entity,
                    true, entity.getExpire());
        }
        consolidateBlobs(entity);
        TaskFactory.getInstance().startCoreTask(null, entity, Action.update, ServerInfoImpl.getFullServerURL(req));

    }



    protected  static void consolidateBlobs(final Entity e) throws NimbitsException {
        final List<ValueBlobStore> stores = ValueTransactionFactory.getDaoInstance(e).getAllStores();
        if (! stores.isEmpty()) {

            log.info("Consolidating " + stores.size() + " blob stores");
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
            SystemServiceFactory.getInstance().updateSystemPoint("Fragmented Dates Merges By Point Maint", dupDates.size(), false);
            for (Long l : dupDates) {
               ValueTransactionFactory.getDaoInstance(e).consolidateDate(new Date(l));

            }
        }
    }


}

