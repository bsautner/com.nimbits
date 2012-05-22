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

package com.nimbits.server.api;



import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.instance.*;

import com.nimbits.server.com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import com.nimbits.server.com.nimbits.server.transactions.dao.instance.InstanceTransactions;
import com.nimbits.server.gson.*;

import javax.annotation.Resource;
import javax.servlet.http.*;
import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/13/11
 * Time: 4:26 PM
 */
public class ServersServletImpl  extends HttpServlet {


    private InstanceTransactions instanceTransactions;
    private EntityJPATransactions entityTransactions;

    @Resource(name="instanceDao")
    public void setInstanceTransactions(InstanceTransactions transactions) {
        this.instanceTransactions = transactions;
    }

    @Resource(name="entityDao")
    public void setEntityTransactions(EntityJPATransactions transactions) {
        this.entityTransactions = transactions;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse  response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("not implemented");
     }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse  response) throws IOException {
        String json = request.getParameter(Parameters.json.getText());
        PrintWriter out = response.getWriter();
        Instance server = GsonFactory.getInstance().fromJson(json, InstanceModel.class);
        Instance retObj = null;
        try {
            retObj = instanceTransactions.addUpdateInstance(server);
        } catch (NimbitsException e) {
            out.println(retObj);
        }
        String r = GsonFactory.getInstance().toJson(retObj);
        out.println(r);
        out.close();
    }
}
