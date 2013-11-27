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

package com.nimbits.server.process.cron;


import com.nimbits.client.constants.Const;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class IdlePointCron extends CronBase {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(IdlePointCron.class.getName());
    private ValueService valueService;
    private com.nimbits.server.transaction.user.service.UserService userService;

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        setup( );
        valueService = ValueServiceFactory.getInstance(engine, taskService);
        userService = AuthenticationServiceFactory.getInstance(engine);
        processGet();

        resp.setStatus(HttpServletResponse.SC_OK);


    }

    public int processGet() {
        final List<Entity> points = entityService.getIdleEntities();
        log.info("Processing " + points.size() + " potentially idle points");
        User admin = userService.getAdmin();
        for (final Entity p : points) {

            valueService.checkIdle(admin, (Point) p);

        }
        return points.size();
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
