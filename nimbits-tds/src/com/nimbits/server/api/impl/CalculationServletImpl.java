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

package com.nimbits.server.api.impl;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.calculation.CalculationServiceFactory;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 3:52 PM
 */
public class CalculationServletImpl extends ApiServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        try {
            init(req, resp, ExportType.plain);
            Calculation c = GsonFactory.getInstance().fromJson(getParam(Parameters.json), CalculationModel.class);
            EntityName name;
            name = CommonFactoryLocator.getInstance().createName(getParam(Parameters.name), EntityType.calculation);
            if ((user != null) && (!user.isRestricted()) && (c != null)) {
                CalculationServiceFactory.getInstance().addUpdateCalculation(user, null, name, c);
            }
        } catch (NimbitsException ignored) {

        }


    }
}
