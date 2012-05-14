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

package com.nimbits.server.process.cron;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.server.transactions.service.subscription.*;
import com.nimbits.server.transactions.service.user.*;
import com.nimbits.server.transactions.service.value.*;
import org.apache.commons.lang3.exception.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class IdlePointCron extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(IdlePointCron.class.getName());

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        // PrintWriter out;
        // out = resp.getWriter();
        try {
            processGet();
        } catch (NimbitsException e) {
            LogHelper.logException(IdlePointCron.class, e);
        }

    }

    protected static int processGet() throws NimbitsException {
        final List<Entity> points =  EntityServiceFactory.getInstance().getIdleEntities();
        log.info("Processing " + points.size() + " potentially idle points");
        for (final Entity p : points) {
            try {
                checkIdle((Point) p);
            } catch (NimbitsException e) {
                log.severe(e.getMessage());
                log.severe(ExceptionUtils.getStackTrace(e));
            }
        }
        return points.size();
    }

    protected static boolean checkIdle(final Point p) throws NimbitsException {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, p.getIdleSeconds() * -1);
        boolean retVal = false;
        final List<Entity> result = EntityServiceFactory.getInstance().getEntityByKey(UserServiceFactory.getServerInstance().getAdmin(),
                p.getOwner(), EntityType.user);
        if (! result.isEmpty()) {
            final User u = (User) result.get(0);
            final Value v = ValueServiceFactory.getInstance().getCurrentValue(p);
            if (p.getIdleSeconds() > 0 && v != null &&
                    v.getTimestamp().getTime() <= c.getTimeInMillis() &&
                    !p.getIdleAlarmSent()) {
                p.setIdleAlarmSent(true);
                EntityServiceFactory.getInstance().addUpdateEntity(u, p);
                // PointServiceFactory.getInstance().updatePoint(u, p);
                final Value va = ValueFactory.createValueModel(v, AlertType.IdleAlert);
                SubscriptionServiceFactory.getInstance().processSubscriptions(u, p,va);
                retVal = true;
            }
        }
        return retVal;
    }


}
