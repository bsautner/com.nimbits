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

package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.client.model.user.UserModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;



public class SessionApi extends ApiBase {

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws IOException, ServletException {

        final PrintWriter out = resp.getWriter();
        setup(req, resp);


        if (user != null && !user.isRestricted()) {
            String json = GsonFactory.getInstance().toJson(user, UserModel.class);
            out.print(json);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.close();
        }



    }
}
