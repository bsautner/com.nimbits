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

package com.nimbits.server.task;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModel;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.email.EmailServiceFactory;
import com.nimbits.server.entity.*;
import com.nimbits.server.facebook.FacebookFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.instantmessage.IMFactory;
import com.nimbits.server.intelligence.IntelligenceServiceFactory;
import com.nimbits.server.math.EquationSolver;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;
import com.nimbits.server.subscription.*;
import com.nimbits.server.twitter.TwitterServiceFactory;
import com.nimbits.server.user.*;
import com.nimbits.shared.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class RecordValueTask extends HttpServlet {

    private static final Logger log = Logger.getLogger(RecordValueTask.class.getName());
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        final Gson gson = GsonFactory.getInstance();
        final String userJson = req.getParameter(Const.PARAM_JSON_USER);
        final String pointJson = req.getParameter(Const.PARAM_JSON_POINT);
        final String valueJson = req.getParameter(Const.PARAM_JSON_VALUE);
        final String loopFlagParam = req.getParameter(Const.PARAM_LOOP);

        final Point point = gson.fromJson(pointJson, PointModel.class);
        final Value value = gson.fromJson(valueJson, ValueModel.class);
        final String note = req.getParameter(Const.PARAM_NOTE);
        final boolean loopFlag = Boolean.valueOf(loopFlagParam);


        final User u;

        double lat = 0.0;
        double lng = 0.0;

        try {
            u = gson.fromJson(userJson, UserModel.class);
        } catch (JsonParseException e) {
            log.severe("Error parsing user json from record value task");
            log.severe(point.getName().getValue());
            log.severe(userJson);
            return;
        }


        try {
            lat = Double.valueOf(req.getParameter(Const.PARAM_LAT));
            lng = Double.valueOf(req.getParameter(Const.PARAM_LNG));
        } catch (NumberFormatException ignored) {

        }

        try {


            if (!loopFlag) {

                if (point.getCalculation() != null && point.getCalculation().getEnabled() && ! Utils.isEmptyString(point.getCalculation().getTarget())) {


                    doCalculation(u, point, value, note, lat, lng);

                }

                if (point.getIntelligence() != null && point.getIntelligence().getEnabled()) {
                    processIntelligence(u, point);
                }

            }


        } catch (NimbitsException e) {
            log.severe(e.getMessage());

        }
        SubscriptionServiceFactory.getInstance().processSubscriptions(point, value);


    }

    private void processIntelligence(final User u,
                                     final Point point) throws NimbitsException {

        log.info("Processing Intelligence");

        final String input = IntelligenceServiceFactory.getInstance().addDataToInput(u, point);
        if (!Utils.isEmptyString(input)) {
            Point targetPoint = PointServiceFactory.getInstance().getPointByID(u, point.getIntelligence().getTargetPointId());
            if (targetPoint != null && targetPoint.getUserFK() == u.getId()) {
                final Value result = IntelligenceServiceFactory.getInstance().processInput(point, targetPoint, input);

                if (result != null) {
                    log.info("got result:" + result.getData());
                    RecordedValueServiceFactory.getInstance().recordValue(u, targetPoint, result, true);
                }

            }
        }



    }


    private void doCalculation(
            final User u,
            final Point point,
            final Value value,
            final String note,
            final double lat,
            final double lng
    ) throws NimbitsException {

        if (! Utils.isEmptyString(point.getCalculation().getTarget())) {
            final Point target = PointServiceFactory.getInstance().getPointByUUID(point.getCalculation().getTarget());

            if (!(target == null)) {
                final double calcResult = EquationSolver.solveEquation(point, u);

                final Value v = ValueModelFactory.createValueModel(lat, lng, calcResult, value.getTimestamp(), target.getUUID(), note);
                RecordedValueServiceFactory.getInstance().recordValue(u, target, v, true);


            }
        }
    }





}