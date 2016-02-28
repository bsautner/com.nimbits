/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api.xmpp;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.io.command.CommandListener;
import com.nimbits.client.io.command.TerminalCommand;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.instance.InstanceModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.ApiBase;
import com.nimbits.server.communication.xmpp.XmppService;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.json.JsonHelper;
import com.nimbits.server.system.ServerInfo;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


@Service
public class XMPPReceiverServlet extends ApiBase {


    private static final Pattern COMPILE = Pattern.compile("/");


    @Autowired
    private ServerInfo serverInfo;

    @Autowired
    private UserService userService;

    @Autowired
    private SettingsService settingsService;



    @Autowired
    private XmppService xmppService;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {


        final XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        final Message message = xmpp.parseMessage(req);
        final JID fromJid = message.getFromJid();
        final String body = message.getBody();
        final String j[] = COMPILE.split(fromJid.getId());
        final String email = j[0].toLowerCase();

        final Optional<Entity> optional = entityDao.getEntityByKey(userService.getAdmin(), email, EntityType.user);

        if (optional.isPresent()) {
        final User user = (User) optional.get();

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

                }

                @Override
                public void onTreeUpdated(List<Entity> newTree) {

                }
            };
            String[] args = body.split(" ");
            TerminalCommand terminalCommand = TerminalCommand.lookup(args[0]);
            if (terminalCommand != null) {
                UrlContainer urlContainer = UrlContainer.getInstance(serverInfo.getFullServerURL(req));
                AccessKey accessToken = new AccessKeyModel.Builder().code(settingsService.getSetting(ServerSetting.token)).create();

                EmailAddress emailAddress = CommonFactory.createEmailAddress(email);
                Instance instance = new InstanceModel.Builder().apiKey(accessToken).adminEmail(emailAddress).baseUrl(urlContainer).create();

                try {
                    List<Entity> tree = entityService.getEntities(user);

                    terminalCommand.init(user, user, instance, tree).doCommand(listener, args);
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


        Gson gson = GsonFactory.getInstance(true);

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(body).getAsJsonArray();
        Action action = gson.fromJson(array.get(0), Action.class);
        Point p = gson.fromJson(array.get(1), PointModel.class);


        switch (action) {
            case record:
                //  Point point = PointServiceFactory.getInstance().getPointByKey(p.getKey());
                Optional<Entity> optional = entityDao.getEntityByKey(u, p.getKey(), EntityType.point);

                if (optional.isPresent()) {


                  //  taskService.process(calculationService, u, (Point) optional.get(), p.getValue(), false);


                    String result = gson.toJson(optional.get());
                    xmppService.sendMessage(result, u.getEmail());

                }

                break;
        }
    }


}