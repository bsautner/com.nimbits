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

import com.google.appengine.api.datastore.*;
import com.google.gwt.core.client.GWT;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


public class DeleteRecordedValuesTask extends HttpServlet {

//    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        final String pointID = req.getParameter(Const.PARAM_POINT_ID);
        final String name = req.getParameter(Const.PARAM_NAME);
        final String exp = req.getParameter(Const.PARAM_EXP);


        // delete me context.addTrace(DeleteRecordedValuesTask.class.getName());
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(name);
        int expDays = 0;
        try {
            final long id = Long.parseLong(pointID);

            if (exp != null) {
                expDays = Integer.parseInt(exp);
                deleteData(id, true, expDays, pointName);
            } else {
                deleteData(id, false, expDays, pointName);

            }
        } catch (NumberFormatException e) {
            GWT.log(e.getMessage());
        }
    }


    private void deleteData(final long pointId, final boolean expOnly, final int expDays, final EntityName name) {
        long count = 0;
        final Set<Key> keys = new HashSet<Key>();
        final Calendar d = Calendar.getInstance();
        d.add(Calendar.DATE, (expDays * -1));
        final DatastoreService store = DatastoreServiceFactory.getDatastoreService();
        final Query q = new Query("RecordedValue").setKeysOnly();
        q.addFilter("pointFK", Query.FilterOperator.EQUAL, pointId);
        if (expOnly) {
            q.addFilter(Const.PARAM_TIMESTAMP, Query.FilterOperator.LESS_THAN, d.getTime());
        }
        for (final Entity e : store.prepare(q).asList(FetchOptions.Builder.withLimit(5000))) {
            count++;
            keys.add(e.getKey());
        }
        if (count > 0) {
            store.delete(keys);
            TaskFactoryLocator.getInstance().startDeleteDataTask(pointId, expOnly, expDays, name);
        }

    }

}
