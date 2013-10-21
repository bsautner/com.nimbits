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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Author: Benjamin Sautner
 * Date: 2/2/13
 * Time: 12:08 PM
 */


public class TimeApi  extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      //  setup(req, resp, false);
        //SettingsService service = SettingServiceFactory.getServiceInstance(engine);
        //Map list= service.getSettings();


        Long time = new Date().getTime();
        String json =  gson.toJson(time);

        PrintWriter out = resp.getWriter();
        out.print(json);
//        completeResponse(resp, String.valueOf(list.size()));

    }
}
