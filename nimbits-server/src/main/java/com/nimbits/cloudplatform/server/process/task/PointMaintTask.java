/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.process.task;

import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * consolidated data from cache into blobstore
 */
@Service("pointTask")
public class PointMaintTask extends HttpServlet  implements org.springframework.web.HttpRequestHandler{




    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

            processPost(req);


    }


    public void processPost(final HttpServletRequest req) throws IOException {


        final String j = req.getParameter(Parameters.json.getText());
        final Point entity = GsonFactory.getInstance().fromJson(j, PointModel.class);
        if (entity.getExpire() > 0) {
            TaskImpl.startDeleteDataTask(
                    entity,
                    true, entity.getExpire());
        }
        consolidateBlobs(entity);
    }



    public void consolidateBlobs(final Entity entity) throws IOException {
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

            for (Long l : dupDates) {
                ValueTransaction.consolidateDate(entity, new Date(l));

            }
        }
    }



}

