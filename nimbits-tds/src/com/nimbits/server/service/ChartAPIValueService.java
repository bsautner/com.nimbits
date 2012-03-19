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

package com.nimbits.server.service;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.net.*;


public class ChartAPIValueService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        String pointNameParam = req.getParameter(Const.Params.PARAM_POINT);
        String uuid = req.getParameter(Const.PARAM_UUID);

        try {
            final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            if (u != null && pointNameParam != null) {
                EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                processRequest(resp, pointName, uuid, u);
            }
        } catch (IOException ignore) {

        } catch (NimbitsException ignore) {

        }
    }

    private void processRequest(HttpServletResponse resp, EntityName pointName, String uuid, User u) throws IOException {
        OutputStream out;
        Value nv;
        out = resp.getOutputStream();
        Point p;

        if (uuid != null) {
            p = PointServiceFactory.getInstance().getPointByUUID(uuid);
        } else if (pointName != null) {
            Entity e = EntityServiceFactory.getInstance().getEntityByName(u, pointName);
            p = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());

        } else {
            p = null;
        }

        if (p != null) {
            Entity e = EntityServiceFactory.getInstance().getEntityByUUID(p.getUUID());
            if (u.isRestricted() && !e.getProtectionLevel().equals(ProtectionLevel.everyone)) {
                //	result = ("Unable to process. You didn't provide an oauth token or secret, and the point you requested is not public");
            } else {
                String s;
                nv = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                if (nv.getNote() != null) {
                    s = nv.getNote() + "  " + nv.getNumberValue();
                } else {
                    s = "" + nv.getNumberValue();
                }
                s = URLEncoder.encode(s, Const.CONST_ENCODING);
                final String postUrl = "http://chart.apis.google.com/chart";
                final String params = "chst=d_text_outline&chld=000000|16|h|FFFFFf|_|"
                        + s;

                URL url = new URL(postUrl);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setReadTimeout(10000);
                OutputStreamWriter writer = new OutputStreamWriter(
                        connection.getOutputStream());

                writer.write(params);

                writer.close();

                InputStream is = connection.getInputStream();
                resp.setContentType("image/png");
                resp.setContentLength(10240);

                byte[] buffer = new byte[10240];
                for (int i; (i = is.read(buffer)) >= 0; ) {
                    out.write(buffer, 0, i);
                }


                out.close();


            }
        }
    }
}
