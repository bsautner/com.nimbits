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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.task;

import com.google.gson.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.email.*;
import com.nimbits.server.facebook.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.instantmessage.*;
import com.nimbits.server.intelligence.*;
import com.nimbits.server.math.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.twitter.*;
import com.nimbits.server.user.*;
import com.nimbits.shared.*;

import javax.servlet.http.*;
import java.util.*;
import java.util.logging.*;

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

        boolean alarmSent;
        log.info(pointJson);
        log.info(valueJson);
        log.info(userJson);
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

            alarmSent = pointDataRelay(u, point, value, note);
            if (!loopFlag) {

                if (point.getCalculation() != null && point.getCalculation().getEnabled() && point.getCalculation().getTarget() > 0) {


                    doCalculation(u, point, value, note, lat, lng);

                }

                if (point.getIntelligence() != null && point.getIntelligence().getEnabled()) {
                    processIntelligence(u, point);
                }

            }

            // PointTransactionsFactory.getInstance().updatePointStats(u, point, value, alarmSent);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());

        }
        processSubscriptions(point, value);


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

        if (point.getCalculation().getTarget() > 0) {
            final Point target = PointServiceFactory.getInstance().getPointByID(u, point.getCalculation().getTarget());

            if (!(target == null)) {
                final double calcResult = EquationSolver.solveEquation(point, u);

                final Value v = ValueModelFactory.createValueModel(lat, lng, calcResult, value.getTimestamp(), target.getId(), note);
                RecordedValueServiceFactory.getInstance().recordValue(u, target, v, true);


            }
        }
    }

    private boolean pointDataRelay(final User u, final Point point, final Value v, final String note) throws NimbitsException {

        boolean alarmSent = false;
        if (point.isHighAlarmOn() || point.isLowAlarmOn()) {
            alarmSent = doAlert(u, point, v, note);
        }
        if (point.isPostToFacebook() && (!alarmSent)) {
            postToFB(point, u, v.getNumberValue(), note);
        }
        if (point.getSendIM()) {
            doXMPP(u, point, v);
        }
        if (point.getSendTweet()) {

            TwitterServiceFactory.getInstance().sendTweet(u, "#" + point.getName().getValue() + " updated to " + v.getNumberValue() + " on #Nimbits");

        }

        return alarmSent;
    }

    private void processSubscriptions(final Point point, final Value v) {
        List<Subscription> subscriptions= PointServiceFactory.getInstance().getSubscriptionsToPoint(point);
        for (Subscription subscription : subscriptions) {

            if (subscription.getLastSent().getTime() + (subscription.getMaxRepeat() * 60 * 1000) < new Date().getTime()) {
                User subscriber = UserServiceFactory.getInstance().getUserByUUID(subscription.getSubscriberUUID());
                AlertType alert = null;
                if (! subscription.getAlertStateChangeMethod().equals(SubscriptionDeliveryMethod.none)){
                    alert = PointServiceFactory.getInstance().getPointAlertState(point, v);
                    if (! alert.equals(AlertType.OK)) {
                        PointServiceFactory.getInstance().updateSubscriptionLastSent(subscription);
                        switch (subscription.getAlertStateChangeMethod()) {
                            case none:
                                break;
                            case email:
                                EmailServiceFactory.getInstance().sendAlert(point, subscriber.getEmail(), v.getNumberValue(),
                                        alert);
                                break;
                            case facebook:
                                postToFB(point, subscriber, v.getNumberValue(), v.getNote());
                                break;
                            case twitter:
                                sendTweet(subscriber, point, v);
                                break;
                            case instantMessage:
                                doXMPP(subscriber, point, v);
                                break;
                        }
                    }
                }
                if (! subscription.getDataUpdateAlertMethod().equals(SubscriptionDeliveryMethod.none)) {
                    PointServiceFactory.getInstance().updateSubscriptionLastSent(subscription);

                    switch (subscription.getDataUpdateAlertMethod()) {

                        case none:
                            break;
                        case email:
                            EmailServiceFactory.getInstance().sendAlert(point, subscriber.getEmail(), v.getNumberValue(),
                                    alert);
                            break;
                        case facebook:
                            postToFB(point, subscriber, v.getNumberValue(), v.getNote());
                            break;
                        case twitter:
                            sendTweet(subscriber, point, v);
                            break;
                        case instantMessage:
                            doXMPP(subscriber, point, v);
                            break;
                    }
                }
            }




        }

    }


    private void doXMPP(final User u, final Point point, final Value v) {
        final String message;

        if (point.getSendAlertsAsJson()) {
            point.setValue(v);
            final String json = GsonFactory.getInstance().toJson(point);
            message = json;
        } else {
            message = "Nimbits Data Point [" + point.getName().getValue()
                    + "] updated to new value: " + v.getNumberValue();
        }


        IMFactory.getInstance().sendMessage(message, u.getEmail());
    }

    private boolean doAlert(final User u, final Point point,
                            final Value v, final String note) throws NimbitsException {

        boolean retVal = false;
        long delay = point.getAlarmDelay() * 60L * 1000L;
        if (point.getLastAlarmSent() != null) {
            if (new Date().getTime() > point.getLastAlarmSent().getTime()
                    + delay) {

                if (point.isHighAlarmOn() && (v.getNumberValue() >= point.getHighAlarm())) {
                    retVal = relayAlert(u, point, v, note, AlertType.HighAlert);
                }
                if (point.isLowAlarmOn() && v.getNumberValue() <= point.getLowAlarm()) {
                    retVal = relayAlert(u, point, v, note, AlertType.LowAlert);
                }
            }
        }
        return retVal;
    }

    private boolean relayAlert(final User u, final Point point,
                               final Value v, final String note, final AlertType anAlertType) throws NimbitsException {

        boolean alarmSent;

        if (point.isAlarmToEmail()) {
            EmailServiceFactory.getInstance().sendAlert(point, u.getEmail(), v.getNumberValue(), anAlertType);
        }


        if (point.getAlarmToFacebook()) {
            postToFB(point, u, v.getNumberValue(), note);
        }
        if (point.getSendAlarmTweet()) {

            sendTweet(u, point, v);

        }
        if (point.getSendAlarmIM()) {
            doXMPP(u, point, v);
        }
        alarmSent = true;
        //PointTransactionsFactory.getInstance().updatePointStats(context, u, point, v, alarmSent);
        // point.setLastAlarmSent(new Date().getTime());
        // PointTransactionsFactory.getInstance().updatePoint(point);

        return alarmSent;
    }

    private void sendTweet(User u, Point point, Value v)  {
        StringBuilder message = new StringBuilder();
        message.append("#").append(point.getName().getValue()).append(" ");
        message.append("Value=").append(v.getNumberValue());
        if (!Utils.isEmptyString(v.getNote())) {
            message.append(" ").append(v.getNote());
        }
        message.append(" via #Nimbits");
        TwitterServiceFactory.getInstance().sendTweet(u, message.toString());
    }

    private void postToFB(final Point p, final User u, final double v, final String note) {
        log.info("posting to facebook: " + p.getName().getValue());
        String m = ("Data Point #" + p.getName().getValue() + " = " + v);
        if (note != null) {
            m += " " + note;
        }

        String picture;


        if (p.isPublic()) {
            picture = "http://app.nimbits.com" +
                    "/service/chartapi?" +
                    "point=" + p.getName().getValue() +
                    "&email=" + u.getEmail().getValue() +
                    "&cht=lc" +
                    "&chs=100x100" +
                    "&chds=a";
            log.info("facebook image: " + picture);
        } else {
            picture = "http://app.nimbits.com/resources/images/logo.png";
        }

        // String link = "http://app.nimbits.com?view=chart&uuid=" + p.getUUID();
        String link = "http://www.nimbits.com";
        FacebookFactory.getInstance().updateStatus(u.getFacebookToken(), m, picture, link, "via Nimbits",
                "Subscribe To this Date Channel", "Point Description: " + p.getDescription());


    }
}