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

package com.nimbits.server.api;

import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



public class SessionApi extends ApiBase {

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws ServletException {


        setup(req, resp, false);


        if (user != null && !user.isRestricted()) {
            HttpSession session = req.getSession();
            user.setSessionId(session.getId());
            String json = GsonFactory.getInstance().toJson(user, UserModel.class);
            completeResponse(resp, json);
        }
        else {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "");
        }



    }
}
