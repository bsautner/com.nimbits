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

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.process.task.TaskFactory;

import com.nimbits.server.transactions.service.entity.EntityServiceImpl;
import com.nimbits.server.transactions.service.user.UserServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
@Service("pointCron")
@Transactional
public class PointCron extends HttpServlet implements org.springframework.web.HttpRequestHandler {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointCron.class.getName());
    private UserServiceImpl userService;
    private EntityServiceImpl entityService;

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {




            try {
                final User admin =userService.getAdmin();

                final Map<String,Entity> e =
                        entityService.getSystemWideEntityMap(admin, EntityType.point);

                for (final Entity en : e.values()) {
                    TaskFactory.getInstance().startPointMaintTask(en);
                }

            } catch (NimbitsException e1) {
                LogHelper.logException(this.getClass(), e1);
            }

        }


    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public UserServiceImpl getUserService() {
        return userService;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public void setEntityService(EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    public EntityServiceImpl getEntityService() {
        return entityService;
    }
}
