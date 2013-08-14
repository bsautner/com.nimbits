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

package com.nimbits.cloudplatform.server.api.impl;

import com.nimbits.cloudplatform.client.enums.SettingType;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.api.ValueApi;
import com.nimbits.cloudplatform.server.transactions.settings.SettingsServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Author: Benjamin Sautner
 * Date: 1/13/13
 * Time: 1:13 PM
 */
@Service("versionApi")
public class VersionApi extends ApiServlet implements org.springframework.web.HttpRequestHandler {
    final Logger log = Logger.getLogger(ValueApi.class.getName());

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter out = resp.getWriter();
        try {
            out.print(SettingsServiceImpl.getSetting(SettingType.serverVersion.getName()));
            out.close();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(e.getMessage());


        }
    }

}
