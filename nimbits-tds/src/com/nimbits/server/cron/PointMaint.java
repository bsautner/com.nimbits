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
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.task.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class PointMaint extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointMaint.class.getName());

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

            try {
                final Map<String,Entity> e = EntityTransactionFactory.getDaoInstance(null).getSystemWideEntityMap(EntityType.point);
                for (final Entity en : e.values()) {
                    TaskFactory.getInstance().startPointMaintTask(en);
                }

            } catch (NimbitsException e1) {
              log.severe(e1.getMessage());
            }

        }

}
