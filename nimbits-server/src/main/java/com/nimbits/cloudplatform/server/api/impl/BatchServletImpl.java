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

import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.server.api.ApiServlet;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service("batch")
public class BatchServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {


    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) {

        if (isPost(req)) {

            doPost(req, resp);
        }
        else {
            doGet(req, resp);
        }

    }
    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {



        try {
            doInit(req, resp, ExportType.plain);


            TaskImpl.startProcessBatchTask(user, req, resp);

            resp.flushBuffer();
            resp.setContentLength(0);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        }

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            doInit(req, resp, ExportType.plain);
            TaskImpl.startProcessBatchTask(user, req, resp);

            resp.flushBuffer();
            resp.setContentLength(0);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        } catch (Exception e) {
           resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           resp.addHeader("ERROR", e.getMessage());
        }
    }



}
