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

import com.google.gwt.core.client.GWT;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DeleteRecordedValuesTask extends HttpServlet {

//    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        final String pointJson = req.getParameter(Parameters.json.getText());
        final String exp = req.getParameter(Parameters.exp.getText());
        Point point = GsonFactory.getInstance().fromJson(pointJson, PointModel.class);

          int expDays = 0;
        try {

            if (exp != null) {
                expDays = Integer.parseInt(exp);
                deleteData(point, true, expDays);
            } else {
                deleteData(point, false, expDays);

            }
        } catch (NumberFormatException e) {
            GWT.log(e.getMessage());
        }
    }

     //TODO - delete blobs
    private void deleteData(final Point point, final boolean expOnly, final int expDays) {
//        long count = 0;
//        final Set<Key> keys = new HashSet<Key>();
//        final Calendar d = Calendar.getInstance();
//        d.add(Calendar.DATE, (expDays * -1));
//        final DatastoreService store = DatastoreServiceFactory.getDatastoreService();
////        Point p = PointServiceFactory.getInstance().getPointByUUID(uuid);
//
//        final Query q = new Query("RecordedValue").setKeysOnly();
//
//        q.addFilter("pointFK", Query.FilterOperator.EQUAL, point.getId());
//        if (expOnly) {
//            q.addFilter(Parameters.timestamp.getText(), Query.FilterOperator.LESS_THAN, d.getTime());
//        }
//        for (final Entity e : store.prepare(q).asList(FetchOptions.Builder.withLimit(5000))) {
//            count++;
//            keys.add(e.getKey());
//        }
//        if (count > 0) {
//            store.delete(keys);
//            TaskFactory.getInstance().startDeleteDataTask(point, expOnly, expDays);
//        }

    }

}
