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

package com.nimbits.server.process.cron;


import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.subscription.SubscriptionService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.logging.LogHelper;

import com.nimbits.server.transactions.service.user.UserServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;


@Service("idleCron")
@Transactional
public class IdlePointCron extends HttpServlet implements org.springframework.web.HttpRequestHandler{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(IdlePointCron.class.getName());
    private EntityService entityService;
    private ValueService valueService;
    private SubscriptionService subscriptionService;
    private UserServiceImpl userService;

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        // PrintWriter out;
        // out = resp.getWriter();
        try {
            processGet();
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NimbitsException e) {
            LogHelper.logException(IdlePointCron.class, e);
        }

    }

    protected int processGet() throws NimbitsException {
        final List<Entity> points =  entityService.getIdleEntities();
        log.info("Processing " + points.size() + " potentially idle points");
        for (final Entity p : points) {
            try {
                checkIdle((Point) p);
            } catch (NimbitsException e) {

                LogHelper.logException(IdlePointCron.class, e);
            }
        }
        return points.size();
    }

    protected boolean checkIdle(final Point p) throws NimbitsException {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, p.getIdleSeconds() * -1);
        boolean retVal = false;
        final List<Entity> result = entityService.getEntityByKey(userService.getAdmin(),
                p.getOwner(), EntityType.user);
        if (! result.isEmpty()) {
            final User u = (User) result.get(0);
            final List<Value> v = valueService.getCurrentValue(p);
            if (p.getIdleSeconds() > 0 && ! v.isEmpty() &&
                    v.get(0).getTimestamp().getTime() <= c.getTimeInMillis() &&
                    !p.getIdleAlarmSent()) {
                p.setIdleAlarmSent(true);
                entityService.addUpdateEntity(u, p);
                // PointServiceFactory.getInstance().updatePoint(u, p);
                final Value va = ValueFactory.createValueModel(v.get(0), AlertType.IdleAlert);
               subscriptionService.processSubscriptions(u, p, va);
                retVal = true;
            }
        }
        return retVal;
    }


    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService getEntityService() {
        return entityService;
    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }

    public void setSubscriptionService(SubscriptionService  subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public UserServiceImpl getUserService() {
        return userService;
    }
}
