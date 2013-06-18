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
import com.nimbits.client.service.settings.SettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Service("systemCron")
@Transactional
public class SystemCron extends HttpServlet implements org.springframework.web.HttpRequestHandler{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private PrintWriter out = null;
    private SettingsService settingsService;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            if (resp != null) {
                resp.setContentType(Const.CONTENT_TYPE_HTML);
                out = resp.getWriter();
            }
            println(Const.HTML_BOOTSTRAP);
            println("<P>" + settingsService.reloadCache() + "</P>");
            if (req != null) {
                println("<p>This Servers URL: " + req.getLocalName() + "</p>");
            }
            println("<h5>Updating Values</h5>");
            for (SettingType setting : SettingType.values()) {
                processSetting(setting);
            }
            //CoreFactory.getInstance().reportInstanceToCore(ServerInfoImpl.getFullServerURL(req));

        } catch (NimbitsException e) {
            println(e.getMessage());
            if (resp != null) {
                resp.setStatus(Const.HTTP_STATUS_INTERNAL_SERVER_ERROR);
            }
        }

        println("<span class=\"label success\">A new Nimbits server has properly initialised!</span>");
        println("<p>You now may want to <A href = \"https://appengine.google.com/\">log into the admin console on App Engine</a> and edit these values to meet your needs.</p>");

        println("</body></html>");
        if (out != null) {
            out.close();
        }
        if (resp != null) {
            resp.setStatus(Const.HTTP_STATUS_OK);
        }

    }

    private void println(String message) {
        if (out != null) {
            out.println(message);
        }
    }


    private void processSetting(final SettingType setting) {

        try {
            final String currentValue = settingsService.getSetting(setting);

            if (setting.isUpdate()) {
                settingsService.updateSetting(setting, setting.getDefaultValue());

                println("<p>" + setting.getName() + " updated to " + setting.getDefaultValue() +
                        " (was " + currentValue + ")</p>");


            }
        } catch (NimbitsException e) {
            if (setting.isCreate()) {
                try {
                    settingsService.addSetting(setting, setting.getDefaultValue());
                    println("<p>Added setting: " + setting.getName() + " new value : " +  setting.getDefaultValue() + "</p>");
                } catch (NimbitsException e1) {
                    println(e.getMessage());
                }

            }
        }


    }




    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }
}
