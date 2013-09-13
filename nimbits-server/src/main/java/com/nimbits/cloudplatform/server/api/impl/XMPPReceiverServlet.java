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

package com.nimbits.cloudplatform.server.api.impl;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.*;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.location.LocationFactory;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueDataModel;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.communication.xmpp.XmppServiceImpl;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.json.JsonHelper;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.user.UserTransactionFactory;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;


@SuppressWarnings("serial")
@Service("xmpp")

public class XMPPReceiverServlet extends ApiServlet implements org.springframework.web.HttpRequestHandler {

    private static final Logger log = Logger.getLogger(XMPPReceiverServlet.class.getName());
    private static final Pattern COMPILE = Pattern.compile("/");
    private static final Pattern PATTERN = Pattern.compile("=");

    private XmppServiceImpl xmppService;

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) throws  IOException {
        User u = null;
        String body = null;
        try {
            final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
            final Message message = xmpp.parseMessage(req);
            final JID fromJid = message.getFromJid();
            body = message.getBody();
            final String j[] = COMPILE.split(fromJid.getId());
            final String email = j[0].toLowerCase();

            log.info("XMPP Message recieved " + email + ":   " + body);
            List<Entity> result = EntityServiceImpl.getEntityByKey(UserTransactionFactory.getInstance().getAdmin(), email, EntityType.user);
            if (! result.isEmpty()) {
                u =  (User) result.get(0);
                u.addAccessKey(UserTransactionFactory.getInstance().authenticatedKey(u));

                if (body.toLowerCase().trim().equals("ls")) {
                    //sendPointList(u);
                } else if (body.indexOf('=') > 0) {

                    recordNewValue(body, u);

                } else if (!body.trim().equals("?") && !body.isEmpty() && body.charAt(body.length() - 1) == '?') {

                    sendCurrentValue(body, u);

                } else if (body.toLowerCase().startsWith("c ")) {
                    xmppService.sendMessage("creating point...", u.getEmail());

                    createPoint(body, u);

                } else if (body.trim().equals("?") || body.toLowerCase().equals("help")) {
                    sendHelp(u);

                } else if (JsonHelper.isJson(body)) { //it's json from the sdk
                    processJson(u, body);
                } else {
                    xmppService.sendMessage(":( I don't understand " + body, u.getEmail());
                }
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            if (u != null) {
                try {
                    xmppService.sendMessage(":-o I don't understand " + body + " " + e.getMessage(), u.getEmail());
                } catch (Exception e1) {
                    log.severe(e.getMessage());
                }
            }
//            if (u != null) {
//                IMFactory.getInstance().sendMessage(e.getMessage(), u.getEmail());
//            }
        }

        // ...
    }

    private void processJson(final User u, final String body) throws Exception {
        log.info(body);

        Gson gson = GsonFactory.getInstance();

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(body).getAsJsonArray();
        Action action = gson.fromJson(array.get(0), Action.class);
        Point p = gson.fromJson(array.get(1), PointModel.class);
        log.info(body);

        switch (action) {
            case record:
                //  Point point = PointServiceFactory.getInstance().getPointByKey(p.getKey());
                Point point = (Point) EntityServiceImpl.getEntityByKey(u, p.getKey(), EntityType.point).get(0);

                if (point != null) {

                    final Value v = ValueTransaction.recordValue(u, point, p.getValue());
                    point.setValue(v);
                    String result = gson.toJson(point);
                    xmppService.sendMessage(result, u.getEmail());
                }

                break;
        }
    }

    private void sendHelp(User u)  {
        xmppService.sendMessage("Usage:", u.getEmail());
        xmppService.sendMessage("? | Help", u.getEmail());
        xmppService.sendMessage("c pointname | Create a data point", u.getEmail());
        xmppService.sendMessage("pointname? | getInstance the current value of a point", u.getEmail());
        xmppService.sendMessage("pointname=3.14 | record a value to that point", u.getEmail());
        xmppService.sendMessage("pointname=Foo Bar | record a text value to that point", u.getEmail());
    }

    private void createPoint(final String body, final User u) throws Exception {


        EntityName pointName = CommonFactory.createName(body.substring(1).trim(), EntityType.point);
        Entity entity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone,
                u.getKey(), u.getKey(), UUID.randomUUID().toString());
        Point p = PointModelFactory.createPointModel(entity,0.0, 90, "", 0.0, false, false, false, 0, false, FilterType.fixedHysteresis, 0.1, false, PointType.basic, 0, false, 0.0 );

        EntityServiceImpl.addUpdateEntity(u, Arrays.<Entity>asList(p));
        //PointServiceFactory.getInstance().addPoint(u, entity);
        xmppService.sendMessage(pointName.getValue() + " created", u.getEmail());



    }

    private void recordNewValue(final CharSequence body, final User u)  {
        String b[] = PATTERN.split(body);
        if (b.length == 2) {

            EntityName pointName = CommonFactory.createName(b[0], EntityType.point);
            String sval = b[1];

            try {
                double v = Double.parseDouble(sval);


                if (u != null) {
                    Value value = ValueFactory.createValueModel(LocationFactory.createLocation(), v, new Date(), "", ValueDataModel.getInstance(SimpleValue.getInstance("")), AlertType.OK);
                    ValueTransaction.recordValue(u, pointName, value);
                }
            } catch (NumberFormatException ignored) {

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }



    private void sendCurrentValue(final String body, final User u) throws Exception {
        if (!Utils.isEmptyString(body) && !body.isEmpty() && body.charAt(body.length() - 1) == '?') {
            final EntityName pointName = CommonFactory.createName(body.replace("?", ""), EntityType.point);

            Entity e = EntityServiceImpl.getEntityByName(u, pointName, EntityType.point).get(0);
            // Point point = PointServiceFactory.getInstance().getPointByKey(e.getKey());
            Entity point = EntityServiceImpl.getEntityByKey(u, e.getKey(), EntityType.point).get(0);

            final List<Value> sample = ValueTransaction.getPrevValue(point, new Date());
            if (! sample.isEmpty()) {
                Value v = sample.get(0);
                String t = "";
                if (v.getNote() != null && !v.getNote().isEmpty()) {
                    t = v.getNote();
                }
                xmppService.sendMessage(e.getName().getValue() + '='
                        + v.getDoubleValue() + ' ' + t, u.getEmail());
            } else {
                xmppService.sendMessage(pointName.getValue() + " has no data", u.getEmail());

            }
        } else {
            xmppService.sendMessage("I don't understand " + body, u.getEmail());

        }


    }



    public void setXmppService(XmppServiceImpl xmppService) {
        this.xmppService = xmppService;
    }

    public XmppServiceImpl getXmppService() {
        return xmppService;
    }
}