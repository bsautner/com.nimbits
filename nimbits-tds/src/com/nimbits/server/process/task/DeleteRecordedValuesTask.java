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

import com.google.gwt.core.client.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.*;
import com.nimbits.server.transactions.service.entity.EntityTransactionFactory;
import com.nimbits.server.transactions.service.user.UserServiceFactory;
import com.nimbits.server.transactions.service.user.UserTransactionFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;

import javax.servlet.http.*;


public class DeleteRecordedValuesTask extends HttpServlet {

//    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        final String pointJson = req.getParameter(Parameters.json.getText());
        Point point = GsonFactory.getInstance().fromJson(pointJson, PointModel.class);


        try {

                deleteData(point);

        } catch (NumberFormatException e) {
            GWT.log(e.getMessage());
        }
    }

     //TODO - delete blobs
    private void deleteData(final Point point)  {
        ValueServiceFactory.getInstance().deleteExpiredData(point);


    }

}
