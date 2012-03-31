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

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.value.RecordedValueTransactionFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 3:55 PM
 */
public class MoveCachedValuesToStoreTask extends HttpServlet

{

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(MoveCachedValuesToStoreTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final String pointJson = req.getParameter(Parameters.point.getText());
        final Point point = GsonFactory.getInstance().fromJson(pointJson, PointModel.class);
        try {
            RecordedValueTransactionFactory.getInstance(point).moveValuesFromCacheToStore();
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }

    }
}
