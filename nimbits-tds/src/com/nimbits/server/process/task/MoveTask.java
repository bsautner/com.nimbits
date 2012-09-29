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

package com.nimbits.server.process.task;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transactions.service.value.ValueServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class MoveTask extends HttpServlet   implements org.springframework.web.HttpRequestHandler

{

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(MoveTask.class.getName());
    private ValueServiceImpl valueService;


    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

        final String pointJson = req.getParameter(Parameters.point.getText());
        final Entity point = GsonFactory.getInstance().fromJson(pointJson, EntityModel.class);
         valueService.moveValuesFromCacheToStore(point);


    }

    public void setValueService(ValueServiceImpl valueService) {
        this.valueService = valueService;
    }

    public ValueServiceImpl getValueService() {
        return valueService;
    }
}
