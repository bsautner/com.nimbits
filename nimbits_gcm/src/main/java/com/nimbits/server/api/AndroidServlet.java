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
import com.nimbits.client.android.AndroidControl;
import com.nimbits.client.android.AndroidControlFactory;
import com.nimbits.server.gson.GsonFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by benjamin on 8/5/13.
 */
public class AndroidServlet extends HttpServlet {

    public static final int DEFAULT_CHART_VALUES = 1000;
    public static final int DEFAULT_TIMER_INTERVAL = 5000;
    public static final double MIN_VERSION = 1.4;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new GsonBuilder().create();
        AndroidControl control = AndroidControlFactory.getInstance(
                DEFAULT_TIMER_INTERVAL,
                DEFAULT_CHART_VALUES,
                MIN_VERSION);
        String json =gson.toJson(control);
        PrintWriter writer = response.getWriter();
        writer.print(json);
        response.setStatus(HttpServletResponse.SC_OK);
        writer.close();


    }
}
