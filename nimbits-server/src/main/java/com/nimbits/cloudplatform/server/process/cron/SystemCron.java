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

package com.nimbits.cloudplatform.server.process.cron;

import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.SettingType;
import com.nimbits.cloudplatform.server.transactions.settings.SettingFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Service("systemCron")
public class SystemCron extends HttpServlet implements org.springframework.web.HttpRequestHandler{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private PrintWriter out = null;


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
          //  println("<P>" + SettingFactory.getServiceInstance().reloadCache() + "</P>");
            if (req != null) {
                println("<p>This Servers URL: " + req.getLocalName() + "</p>");
            }
            println("<h5>Updating Values</h5>");
            for (SettingType setting : SettingType.values()) {
                processSetting(setting);
            }
            //CoreFactory.getInstance().reportInstanceToCore(ServerInfoImpl.getFullServerURL(req));

        } catch (Exception e) {
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
            final String currentValue = SettingFactory.getServiceInstance().getSetting(setting.getName());

            if (setting.isUpdate()) {
                SettingFactory.getServiceInstance().updateSetting(setting.getName(), setting.getDefaultValue());

                println("<p>" + setting.getName() + " updated to " + setting.getDefaultValue() +
                        " (was " + currentValue + ")</p>");


            }
        } catch (Exception e) {
            if (setting.isCreate()) {
                try {
                    SettingFactory.getServiceInstance().addSetting(setting.getName(), setting.getDefaultValue());
                    println("<p>Added setting: " + setting.getName() + " new value : " +  setting.getDefaultValue() + "</p>");
                } catch (Exception e1) {
                    println(e.getMessage());
                }

            }
        }


    }

}
