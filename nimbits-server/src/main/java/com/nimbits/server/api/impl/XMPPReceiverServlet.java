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

package com.nimbits.server.api.impl;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.*;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.simple.SimpleValue;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueDataModel;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.ApplicationListener;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.json.JsonHelper;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


@SuppressWarnings("serial")
public class XMPPReceiverServlet extends ApiServlet {


    private static final Pattern COMPILE = Pattern.compile("/");
    private static final Pattern PATTERN = Pattern.compile("=");


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        User u;
        String body;

        setEngine(ApplicationListener.createEngine());


//            doInit(req, resp, ExportType.json);
        final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        final Message message = xmpp.parseMessage(req);
        final JID fromJid = message.getFromJid();
        body = message.getBody();
        final String j[] = COMPILE.split(fromJid.getId());
        final String email = j[0].toLowerCase();

        List<Entity> result = entityService.getEntityByKey(AuthenticationServiceFactory.getInstance(engine).getAdmin(), email, EntityType.user);
        if (!result.isEmpty()) {
            u = (User) result.get(0);
            u.addAccessKey(AuthenticationServiceFactory.getInstance(engine).authenticatedKey(u));

            if (body.toLowerCase().trim().equals("ls")) {
                //sendPointList(u);
            } else if (body.indexOf('=') > 0) {

                recordNewValue(req, body, u);

            } else if (!body.trim().equals("?") && !body.isEmpty() && body.charAt(body.length() - 1) == '?') {

                sendCurrentValue(body, u);

            } else if (body.toLowerCase().startsWith("c ")) {
                engine.getXmppService().sendMessage("creating point...", u.getEmail());

                createPoint(body, u);

            } else if (body.trim().equals("?") || body.toLowerCase().equals("help")) {
                sendHelp(u);

            } else if (JsonHelper.isJson(body)) { //it's json from the sdk
                processJson(req, u, body);
            } else {
                engine.getXmppService().sendMessage(":( I don't understand you - try ? ", u.getEmail());
            }
        }


        // ...
    }

    private void processJson(HttpServletRequest req, final User u, final String body) {


        Gson gson = GsonFactory.getInstance();

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(body).getAsJsonArray();
        Action action = gson.fromJson(array.get(0), Action.class);
        Point p = gson.fromJson(array.get(1), PointModel.class);


        switch (action) {
            case record:
                //  Point point = PointServiceFactory.getInstance().getPointByKey(p.getKey());
                Point point = (Point) entityService.getEntityByKey(u, p.getKey(), EntityType.point).get(0);

                if (point != null) {

                    final Value v = valueService.recordValue(req, u, point, p.getValue());
                    point.setValue(v);
                    String result = gson.toJson(point);
                    engine.getXmppService().sendMessage(result, u.getEmail());
                }

                break;
        }
    }

    private void sendHelp(User u) {
        engine.getXmppService().sendMessage("Usage:", u.getEmail());
        engine.getXmppService().sendMessage("? | Help", u.getEmail());
        engine.getXmppService().sendMessage("c pointname | Create a data point", u.getEmail());
        engine.getXmppService().sendMessage("pointname? | getInstance the current value of a point", u.getEmail());
        engine.getXmppService().sendMessage("pointname=3.14 | record a value to that point", u.getEmail());
        engine.getXmppService().sendMessage("pointname=Foo Bar | record a text value to that point", u.getEmail());
    }

    private void createPoint(final String body, final User u) {


        EntityName pointName = CommonFactory.createName(body.substring(1).trim(), EntityType.point);
        Entity entity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone,
                u.getKey(), u.getKey(), UUID.randomUUID().toString());
        Point p = PointModelFactory.createPointModel(entity, 0.0, 90, "", 0.0, false, false, false, 0, false, FilterType.fixedHysteresis, 0.1, false, PointType.basic, 0, false, 0.0);

        entityService.addUpdateEntity(u, Arrays.<Entity>asList(p));
        //PointServiceFactory.getInstance().addPoint(u, entity);
        engine.getXmppService().sendMessage(pointName.getValue() + " created", u.getEmail());


    }

    private void recordNewValue(HttpServletRequest req, final CharSequence body, final User u) {
        String b[] = PATTERN.split(body);
        if (b.length == 2) {

            EntityName pointName = CommonFactory.createName(b[0], EntityType.point);
            String sval = b[1];

            try {
                double v = Double.parseDouble(sval);


                if (u != null) {
                    Value value = ValueFactory.createValueModel(LocationFactory.createLocation(), v, new Date(), "", ValueDataModel.getInstance(SimpleValue.getInstance("")), AlertType.OK);
                    valueService.recordValue(req, u, pointName, value);
                }
            } catch (NumberFormatException ignored) {

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }


    private void sendCurrentValue(final String body, final User u) {
        String message = "";
        if (!Utils.isEmptyString(body) && !body.isEmpty() && body.charAt(body.length() - 1) == '?') {
            final EntityName pointName = CommonFactory.createName(body.replace("?", ""), EntityType.point);

            List<Entity> eSample = entityService.getEntityByName(u, pointName, EntityType.point);
            if (!eSample.isEmpty()) {
                // Point point = PointServiceFactory.getInstance().getPointByKey(e.getKey());
                Entity e = eSample.get(0);
                List<Entity> pointSample = entityService.getEntityByKey(u, e.getKey(), EntityType.point);

                if (!pointSample.isEmpty()) {
                    Point point = (Point) pointSample.get(0);
                    final List<Value> sample = valueService.getPrevValue(point, new Date());
                    if (!sample.isEmpty()) {
                        Value v = sample.get(0);
                        String t = "";
                        if (v.getNote() != null && !v.getNote().isEmpty()) {
                            t = v.getNote();
                        }
                        engine.getXmppService().sendMessage(e.getName().getValue() + '='
                                + v.getDoubleValue() + ' ' + t, u.getEmail());
                    } else {
                        engine.getXmppService().sendMessage(pointName.getValue() + " has no data", u.getEmail());

                    }
                } else {
                    message = "was that a data point?";
                }

            } else {
                message = "Entity not found";
            }
        } else {
            message = "I don't understand";

        }
        engine.getXmppService().sendMessage(message + " " + body, u.getEmail());

    }

}