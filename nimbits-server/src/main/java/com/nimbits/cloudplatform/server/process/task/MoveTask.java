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

package com.nimbits.cloudplatform.server.process.task;

import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 3:55 PM
 */
@Service("moveTask")
public class MoveTask extends HttpServlet implements org.springframework.web.HttpRequestHandler

{

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

        final String pointJson = req.getParameter(Parameters.point.getText());
        final Entity point = GsonFactory.getInstance().fromJson(pointJson, EntityModel.class);
        ValueTransaction.moveValuesFromCacheToStore(point);


    }

}
