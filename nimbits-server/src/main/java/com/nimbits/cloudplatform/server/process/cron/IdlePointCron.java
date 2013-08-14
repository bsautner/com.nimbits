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

package com.nimbits.cloudplatform.server.process.cron;


import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.subscription.SubscriptionService;
import com.nimbits.cloudplatform.server.transactions.user.UserTransaction;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;


@Service("idleCron")

public class IdlePointCron extends HttpServlet implements org.springframework.web.HttpRequestHandler {
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
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        resp.setStatus(HttpServletResponse.SC_OK);


    }

    public int processGet() throws Exception {
        final List<Entity> points = EntityServiceImpl.getIdleEntities();
        log.info("Processing " + points.size() + " potentially idle points");
        for (final Entity p : points) {

            checkIdle((Point) p);

        }
        return points.size();
    }

    public boolean checkIdle(final Point p) throws Exception {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, p.getIdleSeconds() * -1);
        boolean retVal = false;
        final List<Entity> result = EntityServiceImpl.getEntityByKey(UserTransaction.getAdmin(),
                p.getOwner(), EntityType.user);
        if (!result.isEmpty()) {
            final User u = (User) result.get(0);
            final List<Value> v = ValueTransaction.getCurrentValue(p);
            if (p.getIdleSeconds() > 0 && !v.isEmpty() &&
                    v.get(0).getTimestamp().getTime() <= c.getTimeInMillis() &&
                    !p.getIdleAlarmSent()) {
                p.setIdleAlarmSent(true);
                EntityServiceImpl.addUpdateEntity(u, Arrays.asList((Entity)p));
                // PointServiceFactory.getInstance().updatePoint(u, p);
                final Value va = ValueFactory.createValueModel(v.get(0), AlertType.IdleAlert);
                SubscriptionService.processSubscriptions(u, p, va);
                retVal = true;
            }
        }
        return retVal;
    }


    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
