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

package com.nimbits.server.xmpp;

import com.google.appengine.api.xmpp.*;
import com.google.gson.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.json.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;


@SuppressWarnings("serial")
public class XMPPReceiverServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(XMPPReceiverServlet.class.getName());

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException {

        try {
            final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
            final Message message = xmpp.parseMessage(req);
            final JID fromJid = message.getFromJid();
            final String body = message.getBody();
            final String j[] = fromJid.getId().split("/");
            final String email = j[0].toLowerCase();
            final EmailAddress internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
            log.info("XMPP Message recieved " + email + ":   " + message);
            User u;

            u = UserTransactionFactory.getInstance().getNimbitsUser(internetAddress);
            if (u != null) {
                if (body.toLowerCase().trim().equals("ls")) {
                    //sendPointList(u);
                } else if (body.indexOf("=") > 0) {

                    recordNewValue(body, u);

                } else if ((!body.trim().equals("?")) && body.endsWith("?")) {

                    sendCurrentValue(body, u);

                } else if (body.toLowerCase().startsWith("c ")) {
                    XmppServiceFactory.getInstance().sendMessage("creating point...", u.getEmail());

                    createPoint(body, u);

                } else if (body.trim().equals("?")) {
                    sendHelp(u);

                } else if (JsonHelper.isJson(body)) { //it's json from the sdk
                    processJson(u, body);
                } else {
                    XmppServiceFactory.getInstance().sendMessage("I received your message but couldn't understand it.", u.getEmail());
                }
            }
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
//            if (u != null) {
//                IMFactory.getInstance().sendMessage(e.getMessage(), u.getEmail());
//            }
        }

        // ...
    }

    private void processJson(User u, String body) throws NimbitsException {
        log.info(body);

        Gson gson = GsonFactory.getInstance();

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(body).getAsJsonArray();
        Action action = gson.fromJson(array.get(0), Action.class);
        Point p = gson.fromJson(array.get(1), PointModel.class);
        log.info(body);

        switch (action) {
            case record:
                Point point = PointServiceFactory.getInstance().getPointByUUID(p.getUUID());

                if (point != null) {
                    log.info("xmpp found point" + point.getId());
                    final Value v = RecordedValueServiceFactory.getInstance().recordValue(u, point, p.getValue(), false);
                    point.setValue(v);
                    String result = gson.toJson(point);
                    XmppServiceFactory.getInstance().sendMessage(result, u.getEmail());
                }

                break;
        }
    }

    private void sendHelp(User u) {
        XmppServiceFactory.getInstance().sendMessage("Usage:", u.getEmail());
        XmppServiceFactory.getInstance().sendMessage("? | Help", u.getEmail());
        XmppServiceFactory.getInstance().sendMessage("c pointname | Create a data point", u.getEmail());
        XmppServiceFactory.getInstance().sendMessage("pointname? | getInstance the current value of a point", u.getEmail());
        XmppServiceFactory.getInstance().sendMessage("pointname=3.14 | record a value to that point", u.getEmail());
        XmppServiceFactory.getInstance().sendMessage("pointname=Foo Bar | record a text value to that point", u.getEmail());
    }

    private void createPoint(final String body, final User u) throws NimbitsException {


        EntityName pointName = CommonFactoryLocator.getInstance().createName(body.substring(1).trim(), EntityType.point);
        Entity entity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone, UUID.randomUUID().toString(),
                u.getUuid(), u.getUuid());
        PointServiceFactory.getInstance().addPoint(u, entity);
        XmppServiceFactory.getInstance().sendMessage(pointName.getValue() + " created", u.getEmail());



    }

    private void recordNewValue(String body, User u) throws NimbitsException {
        String b[] = body.split("=");
        if (b.length == 2) {

            EntityName pointName = CommonFactoryLocator.getInstance().createName(b[0], EntityType.point);
            String sval = b[1];

            try {
                double v = Double.parseDouble(sval);


                if (u != null) {
                    Value value = ValueModelFactory.createValueModel(0.0, 0.0, v, new Date(), null, "");
                    RecordedValueServiceFactory.getInstance().recordValue(u, pointName, value);
                }
            } catch (NumberFormatException ignored) {

            }

        }
    }



    private void sendCurrentValue(final String body, final User u) throws NimbitsException {
        if (!Utils.isEmptyString(body) && body.endsWith("?")) {
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(body.replace("?", ""), EntityType.point);

            Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
            Point point = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());

            String t = "";

            final Value v = RecordedValueServiceFactory.getInstance().getPrevValue(point, new Date());
            if (v != null) {
                if (v.getNote() != null && v.getNote().length() > 0) {
                    t = v.getNote();
                }
                XmppServiceFactory.getInstance().sendMessage(e.getName().getValue() + "="
                        + v.getNumberValue() + " " + t, u.getEmail());
            } else {
                XmppServiceFactory.getInstance().sendMessage(pointName.getValue() + " has no data", u.getEmail());

            }
        } else {
            XmppServiceFactory.getInstance().sendMessage("I don't understand " + body, u.getEmail());

        }


    }
}