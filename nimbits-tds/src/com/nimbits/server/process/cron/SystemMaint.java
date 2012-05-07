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

package com.nimbits.server.process.cron;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.server.*;
import com.nimbits.server.admin.common.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.http.*;
import com.nimbits.server.settings.SettingTransactionsFactory;
import com.nimbits.server.settings.SettingsServiceFactory;

import javax.servlet.http.*;
import java.io.*;

public class SystemMaint extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private PrintWriter out = null;


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
            for (SettingType setting : SettingType.values()) {
                processSetting(setting);
            }
            reportToCore(req);
        } catch (NimbitsException e) {
            out.println(e.getMessage());
        }

        out.println("<span class=\"label success\">A new Nimbits server has properly initialised!</span>");
        out.println("<p>You now may want to <A href = \"https://appengine.google.com/\">log into the admin console on App Engine</a> and edit these values to meet your needs.</p>");

        out.println("</body></html>");
        out.close();
    }



    private void reportToCore(final HttpServletRequest req) throws NimbitsException {
        if (SettingTransactionsFactory.getInstance().getSetting(SettingType.serverIsDiscoverable).equals("1")) {
            out.println("<span class=\"label success\">Because Setting: " + SettingType.serverIsDiscoverable.getDefaultValue() + "" +
                    " has a value of \"1\" your public points and this server will be discoverable on nimbits.com.<br /> This is what was sent to nimbits.com:</span>");
            final String email = SettingTransactionsFactory.getInstance().getSetting(SettingType.admin);
            final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
            final Server server = ServerModelFactory.createServer(ServerInfoImpl.getFullServerURL(req), emailAddress, SettingType.serverVersion.getDefaultValue());
            final String json = GsonFactory.getInstance().toJson(server);
            final String params = Parameters.json.getText() + "=" + json;
            out.println("<p>");
            out.println(HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_SERVERS_URL, params));
            out.println("</p>");
        } else {
            out.println("<span class=\"label success\">Because Setting: " + SettingType.serverIsDiscoverable.getDefaultValue() + "" +
                    " does not have value of \"1\" your public points and this server will NOT be discoverable on nimbits.com. " +
                    " Please consider changing this value to 1 so others can find your points and share data with you.</span>");

        }
    }

    private void processSetting(final SettingType setting) {

        try {
            final String currentValue = SettingTransactionsFactory.getDaoInstance().getSetting(setting);

            if (setting.isUpdate()) {
                SettingsServiceFactory.getInstance().updateSetting(setting,setting.getDefaultValue());

                out.println("<p>" + setting.getName() + " updated to " + setting.getDefaultValue() +
                        " (was " + currentValue + ")</p>");


            }
        } catch (NimbitsException e) {
            if (setting.isCreate()) {
                SettingsServiceFactory.getInstance().addSetting(setting, setting.getDefaultValue());
                out.println("<p>Added setting: " + setting.getName() + " new value : " +  setting.getDefaultValue() + "</p>");
            }
        }


    }





}
