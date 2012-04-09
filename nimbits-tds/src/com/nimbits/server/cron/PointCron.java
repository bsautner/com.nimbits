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

package com.nimbits.server.cron;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.logging.LogHelper;
import com.nimbits.server.orm.*;
import com.nimbits.server.task.TaskFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class
        PointCron extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointCron.class.getName());

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {


            try {
                final Map<String,Entity> e = EntityTransactionFactory.getDaoInstance(null).getSystemWideEntityMap(EntityType.point, PointEntity.class);
                log.info("PointMaint processing " + e.values().size() + " points");
                for (final Entity en : e.values()) {
                    TaskFactory.getInstance().startPointMaintTask(en);
                }

            } catch (NimbitsException e1) {
                LogHelper.logException(this.getClass(), e1);
            }

        }

}
