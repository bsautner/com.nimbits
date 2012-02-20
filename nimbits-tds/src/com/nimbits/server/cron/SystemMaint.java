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

package com.nimbits.server.cron;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModelFactory;
import com.nimbits.server.common.ServerInfoImpl;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.server.settings.SettingTransactionsFactory;
import com.nimbits.server.task.TaskFactoryLocator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SystemMaint extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private PrintWriter out = null;
    private final String I = "UPGRADE_TASK_KEY";
    private final MemcacheService cache = MemcacheServiceFactory.getMemcacheService("UPGRADE_TASK");
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {

            resp.setContentType(Const.CONTENT_TYPE_HTML);
            out = resp.getWriter();
            out.println(Const.HTML_BOOTSTRAP);
            out.println("<P>" + SettingTransactionsFactory.getInstance().reloadCache() + "</P>");
            out.print("<p>This Servers URL: " + req.getLocalName() + "</p>");
            out.println("<h5>Updating Values</h5>");


            processSetting(Const.SETTING_LAST_CHECKED, new Date().toString(), true);
            processSetting(Const.SETTING_VERSION, Const.CONST_SERVER_VERSION, true);

            processSetting(Const.PARAM_SECRET, UUID.randomUUID().toString(), false);
            processSetting(Const.SETTING_ADMIN, Const.TEST_ACCOUNT, false);
            processSetting(Const.SETTING_ENABLE_CONNECTIONS, "1", false);
            processSetting(Const.SETTING_FACEBOOK_CLIENT_ID, "", false);
            processSetting(Const.SETTING_FACEBOOK_REDIRECT_URL, Const.PATH_FACEBOOK_REDIRECT, false);
            processSetting(Const.SETTING_FACEBOOK_API_KEY, "", false);
            processSetting(Const.SETTING_FACEBOOK_SECRET, "", false);
            processSetting(Const.SETTING_WOLFRAM, Const.CONST_UNKNOWN, false);
            processSetting(Const.SETTING_TWITTER_CLIENT_ID, "", false);
            processSetting(Const.SETTING_TWITTER_SECRET, "", false);
            processSetting(Const.SETTING_SERVER_IS_DISCOVERABLE, "1", false);

            reportToCore(req);
        } catch (NimbitsException e) {
            out.println(e.getMessage());
        }

        out.println("<span class=\"label success\">A new Nimbits server has properly initialised!</span>");
        out.println("<p>You now may want to <A href = \"https://appengine.google.com/\">log into the admin console on App Engine</a> and edit these values to meet your needs.</p>");


        out.println("<HTML><BODY>");
        if (cache.contains(I)) {
            out.println("<P>" + "Upgrade Task Dump" + "</P>");
            List<String> log = (List<String>) cache.get(I);
            for (String s : log) {
                out.println("<P>" + s + "</P>");
            }
        }



        out.println("</BODY></HTML>");


        out.println("</body></html>");



        out.close();
    }



    private void reportToCore(final HttpServletRequest req) throws NimbitsException {
        if (SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_SERVER_IS_DISCOVERABLE).equals("1")) {
            out.println("<span class=\"label success\">Because Setting: " + Const.SETTING_SERVER_IS_DISCOVERABLE + "" +
                    " has a value of \"1\" your public points and this server will be discoverable on nimbits.com.<br /> This is what was sent to nimbits.com:</span>");
            final String email = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_ADMIN);
            final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
            final Server server = ServerModelFactory.createServer(ServerInfoImpl.getFullServerURL(req), emailAddress, Const.CONST_SERVER_VERSION);
            final String json = GsonFactory.getInstance().toJson(server);
            final String params = Const.PARAM_JSON + "=" + json;
            out.println("<p>");
            out.println(HttpCommonFactory.getInstance().doPost(Const.PATH_NIMBITS_CORE_SERVERS_URL, params));
            out.println("</p>");
        } else {
            out.println("<span class=\"label success\">Because Setting: " + Const.SETTING_SERVER_IS_DISCOVERABLE + "" +
                    " does not have value of \"1\" your public points and this server will NOT be discoverable on nimbits.com. " +
                    " Please consider changing this value to 1 so others can find your points and share data with you.</span>");

        }
    }

    private void processSetting(final String name, final String value, final boolean update) {

        try {
            final String s = SettingTransactionsFactory.getDaoInstance().getSetting(name);

            if (update) {
                SettingTransactionsFactory.getInstance().updateSetting(name, value);

                out.println("<p>" + name + " updated to " + value + " (was " + s + ")</p>");
                if (name.equals(Const.SETTING_VERSION) && ! s.equals(Const.CONST_SERVER_VERSION) ) {
                    out.println("<p>New Version detected, starting upgrade. This may take up to an hour after " +
                            "after seeing this message. You can monitor this on the app engine console " +
                            "under the task queue. Upgrade is completed after all upgrade tasks have stopped.</p>");
                    TaskFactoryLocator.getInstance().startUpgradeTask(Action.start, null);
                }

            }
        } catch (NimbitsException e) {
            SettingTransactionsFactory.getInstance().addSetting(name, value);
            out.println("<p>Added setting: " + name + " new value : " + value + "</p>");
        }


    }





}
