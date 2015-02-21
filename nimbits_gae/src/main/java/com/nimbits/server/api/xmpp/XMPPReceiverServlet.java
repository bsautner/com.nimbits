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

package com.nimbits.server.api.xmpp;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.AccessCode;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.command.CommandListener;
import com.nimbits.io.command.TerminalCommand;
import com.nimbits.server.system.ServerInfo;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.communication.xmpp.XmppService;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.json.JsonHelper;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


@Service
public class XMPPReceiverServlet extends ApiBase {


    private static final Pattern COMPILE = Pattern.compile("/");


    @Autowired
    private ServerInfo serverInfo;

    @Autowired
    protected UserService userService;

    @Autowired
    protected SettingsService settingsService;

    @Autowired
    private NimbitsCache cache;

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Autowired
    private XmppService xmppService;


    private String getCurrentKey(User user) {
        return user.getKey() + "COMMANDCURRENTKEY";
    }

    private String getTreeKey(User user) {
        return user.getKey() + "COMMANDTREEKEY";
    }

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {


        final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        final Message message = xmpp.parseMessage(req);
        final JID fromJid = message.getFromJid();
        final String body = message.getBody();
        final String j[] = COMPILE.split(fromJid.getId());
        final String email = j[0].toLowerCase();

        List<Entity> result = entityService.getEntityByKey(userService.getAdmin(), email, EntityType.user);
        if (!result.isEmpty()) {
            final User user = (User) result.get(0);
            user.addAccessKey(userService.authenticatedKey(user));

            if (JsonHelper.isJson(body)) {
                processJson(user, body);
            } else {

                CommandListener listener = new CommandListener() {
                    @Override
                    public void onMessage(String message) {
                        xmppService.sendMessage(message, user.getEmail());
                    }

                    @Override
                    public void setCurrent(Entity newCurrent) {
                      cache.put(getCurrentKey(user), newCurrent);
                    }

                    @Override
                    public void onTreeUpdated(List<Entity> newTree) {
                        cache.put(getTreeKey(user), newTree);
                    }
                };
                String[] args = body.split(" ");
                TerminalCommand terminalCommand = TerminalCommand.lookup(args[0]);
                if (terminalCommand != null) {
                    UrlContainer urlContainer = UrlContainer.getInstance(serverInfo.getFullServerURL(req));
                    AccessCode accessCode = AccessCode.getInstance(settingsService.getSetting(ServerSetting.apiKey));
                    EmailAddress emailAddress = CommonFactory.createEmailAddress(email);
                    Server server = ServerFactory.getInstance(urlContainer, emailAddress, accessCode);
                    try {
                        List<Entity> tree;
                        if (cache.contains(getTreeKey(user))) {
                            tree = (List<Entity>) cache.get(getTreeKey(user));
                        }

                        else {
                            tree = Collections.<Entity>emptyList();
                        }
                        if (tree == null || tree.isEmpty() && terminalCommand.usesTree()) {
                            tree = entityService.getEntities(user);
                            cache.put(getTreeKey(user), tree);
                        }
                        terminalCommand.init(user, user, server, tree).doCommand(listener, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    xmppService.sendMessage("command not found", user.getEmail());

                }

            }
        }
    }





    private void processJson(final User u, final String body) {


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

                    final Value v;
                    try {
                        v = valueService.recordValue(u, point, p.getValue(), false);

                        point.setValue(v);
                        String result = gson.toJson(point);
                        xmppService.sendMessage(result, u.getEmail());
                    } catch (ValueException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }





}