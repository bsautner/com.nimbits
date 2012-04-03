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

package com.nimbits.server.task;

import com.google.gson.Gson;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.value.RecordedValueTransactionFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PointMaintTask extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointMaintTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final Gson gson = GsonFactory.getInstance();
        resp.setContentType(Const.CONTENT_TYPE_HTML);

        final String j = req.getParameter(Parameters.json.getText());
        final Entity e = gson.fromJson(j, EntityModel.class);

        try {
            consolidateBlobs(e);


        } catch (Exception ex) {
           log.severe(ex.getMessage());
        }









    }

    public static void consolidateBlobs(final Entity e) throws NimbitsException {
        // n = UserTransactionFactory.getInstance().(p.getUserFK());
        final Point p = PointServiceFactory.getInstance().getPointByKey(e.getKey());
        final List<ValueBlobStore> stores = RecordedValueTransactionFactory.getDaoInstance(p).getAllStores();
        if (stores.size() > 0) {

            final List<Long> dates = new ArrayList<Long>(stores.size());
            for (final ValueBlobStore store : stores) {
                if (dates.contains(store.getTimestamp().getTime())) {
                    RecordedValueTransactionFactory.getDaoInstance(p).consolidateDate(store.getTimestamp());
                }
                else {
                    dates.add(store.getTimestamp().getTime());
                }
            }



        }
    }


}

