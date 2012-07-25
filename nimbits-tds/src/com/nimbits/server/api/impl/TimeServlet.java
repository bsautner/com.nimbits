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

package com.nimbits.server.api.impl;

import com.nimbits.client.common.*;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.gson.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 9:14 AM
 */
public class TimeServlet extends ApiServlet {


    private static final long serialVersionUID = 6160961337851138572L;
    final static Logger log = Logger.getLogger(TimeServlet.class.getName());

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {




            final PrintWriter out = resp.getWriter();
            DateFormatType type;
            final String format = req.getParameter(Parameters.format.getText());
            final String clientTypeParam = req.getParameter(Parameters.client.getText());
            final ClientType clientType;

            if (! Utils.isEmptyString(clientTypeParam)) {
                clientType = ClientType.valueOf(clientTypeParam);
            }
            else {
                clientType = ClientType.other;
            }

            if (Utils.isEmptyString(format)) {
                type = DateFormatType.unixEpoch;

            }
            else {
                type = DateFormatType.get(format);
            }
            if (type == null) {
                type = DateFormatType.unixEpoch;
            }

            if (clientType.equals(ClientType.arduino)) {

                out.print(Const.CONST_ARDUINO_DATA_SEPARATOR);
            }
            switch (type) {

                case unixEpoch:
                    if (clientType.equals(ClientType.arduino)) {
                        out.print(new Date().getTime() / 1000);
                    }
                    else {
                    out.print(new Date().getTime());
                    }
                    break;

                case json:
                    out.print(GsonFactory.getInstance().toJson(new Date()));
                    break;
            }

        if (clientType.equals(ClientType.arduino)) {

            out.print(Const.CONST_ARDUINO_DATA_SEPARATOR);
        }

    }
}
