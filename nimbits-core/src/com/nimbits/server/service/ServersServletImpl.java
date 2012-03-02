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



import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.*;
import com.nimbits.client.model.server.*;

import com.nimbits.server.dao.server.*;
import com.nimbits.server.gson.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/13/11
 * Time: 4:26 PM
 */
public class ServersServletImpl  extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse  response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("not implemented");
     }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse  response) throws IOException {
        String json = request.getParameter(Const.Params.PARAM_JSON);
        PrintWriter out = response.getWriter();
        Server server = GsonFactory.getInstance().fromJson(json, ServerModel.class);
        Server retObj = null;
        try {
            retObj = ServerTransactionFactory.getInstance().addUpdateServer(server);
        } catch (NimbitsException e) {
            out.println(retObj);
        }
        String r = GsonFactory.getInstance().toJson(retObj);
        out.println(r);
        out.close();
    }
}
