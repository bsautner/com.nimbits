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

package com.nimbits.server.process.task;

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueDataModel;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class IncomingMailTask extends ApiBase {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //  private final Map<String, Point> points = new HashMap<String, Point>();
    private static final Logger log = Logger.getLogger(IncomingMailTask.class.getName());
    private static final Pattern COMPILE = Pattern.compile(",");
    private static final Pattern PATTERN = Pattern.compile("\n");
    private static final Pattern COMPILE1 = Pattern.compile("\r");
    private static final Pattern COMPILE2 = Pattern.compile(";");

    private UserService userService;
    private ValueService valueService;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException {
        setup(req, resp);
        valueService = ValueServiceFactory.getInstance(engine, taskService);
        final String fromAddress = req.getParameter(Parameters.fromAddress.getText());
        final String inContent = req.getParameter(Parameters.inContent.getText());

        final EmailAddress internetAddress = CommonFactory.createEmailAddress(fromAddress);


        log.info("Incoming mail post: " + internetAddress);
        //u = UserTransactionFactory.getInstance().getNimbitsUser(internetAddress);
        List<Entity> result = entityService.getEntityByKey(userService.getAdmin(), internetAddress.getValue(), EntityType.user);

        final String content = COMPILE1.matcher(PATTERN.matcher(inContent).replaceAll("")).replaceAll("");
        final String Data[] = COMPILE2.split(content);
        log.info("Incoming mail post: " + inContent);

        if (result.isEmpty()) {
            log.severe("Null user for incoming mail:" + fromAddress);

        } else {
            final User u = (User) result.get(0);
            u.addAccessKey(userService.authenticatedKey(u));
            if (Data.length > 0) {
                for (String s : Data) {
                    processLine(u, s);
                }
            }
        }



    }

    private void processLine(final User u, final CharSequence s)   {
        final String emailLine[] = COMPILE.split(s);
        final EntityName pointName = CommonFactory.createName(emailLine[0], EntityType.point);

        List<Entity> e = entityService.getEntityByName(u, pointName, EntityType.point);


        if (! e.isEmpty()) {
            sendValue(u, e.get(0), emailLine);
        }
    }

    private void sendValue(final User u,
                           final Entity point,
                           final String k[])  {


        if (k != null && k.length > 1) {

            Double v = 0.0;
            try {
                v = Double.valueOf(k[1].trim());
            } catch (NumberFormatException e1) {
                log.info("Invalid mail message from: " + u.getEmail() + ' ' + k[0] + ',' + k[1]);
            }

            long timestamp;
            if (k.length == 3) {

                try {
                    String ts = k[2].trim();
                    timestamp = Long.parseLong(ts);
                } catch (NumberFormatException e) {
                    timestamp = new Date().getTime();
                    log.info("Invalid mail message from: " + u.getEmail() + ' ' + k[0] + ',' + k[1] + ',' + k[2]);
                }
            } else {
                timestamp = new Date().getTime();
            }
            String note = k.length == 4 ? k[3].trim() : "";
            final Value value = ValueFactory.createValueModel(LocationFactory.createLocation(), v, new Date(timestamp), note, ValueDataModel.getEmptyInstance(), AlertType.OK);
            try {
                valueService.recordValue(u, point, value);
            } catch (Exception e) {
                log.severe(e.getMessage());

            }
        }


    }

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService getEntityService() {
        return entityService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }


}
