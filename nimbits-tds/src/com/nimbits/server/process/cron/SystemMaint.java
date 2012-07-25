/*
 * Copyright (c) 2010 Nimbits Inc.
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

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.settings.SettingTransactionsFactory;
import com.nimbits.server.settings.SettingsServiceFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
            //CoreFactory.getInstance().reportInstanceToCore(ServerInfoImpl.getFullServerURL(req));

        } catch (NimbitsException e) {
            out.println(e.getMessage());
        }

        out.println("<span class=\"label success\">A new Nimbits server has properly initialised!</span>");
        out.println("<p>You now may want to <A href = \"https://appengine.google.com/\">log into the admin console on App Engine</a> and edit these values to meet your needs.</p>");

        out.println("</body></html>");
        out.close();
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
                try {
                    SettingsServiceFactory.getInstance().addSetting(setting, setting.getDefaultValue());
                    out.println("<p>Added setting: " + setting.getName() + " new value : " +  setting.getDefaultValue() + "</p>");
                } catch (NimbitsException e1) {
                    out.println(e.getMessage());
                }

            }
        }


    }





}
