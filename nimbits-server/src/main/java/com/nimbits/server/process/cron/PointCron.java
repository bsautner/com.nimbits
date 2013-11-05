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

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class PointCron extends ApiBase {

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException, ServletException {

        setup(req, resp);
        final User admin = AuthenticationServiceFactory.getInstance(engine).getAdmin();
        final Map<String, Entity> e = entityService.getSystemWideEntityMap(admin, EntityType.point);

        for (final Entity en : e.values()) {
            taskService.startPointMaintTask(en);
        }

    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
