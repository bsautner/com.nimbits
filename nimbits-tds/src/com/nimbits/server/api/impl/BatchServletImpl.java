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

import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.process.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Transactional
@Service("batchApi")
public class BatchServletImpl extends ApiServlet implements org.springframework.web.HttpRequestHandler {

    private Task taskFactory;

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {



        try {
            doInit(req, resp, ExportType.plain);


            taskFactory.startProcessBatchTask(user, req, resp);

            resp.flushBuffer();
            resp.setContentLength(0);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        } catch (NimbitsException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        }

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {


        try {
            doInit(req, resp, ExportType.plain);
            taskFactory.startProcessBatchTask(user, req, resp);

            resp.flushBuffer();
            resp.setContentLength(0);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.addHeader("ERROR", e.getMessage());
        } catch (NimbitsException e) {
           resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           resp.addHeader("ERROR", e.getMessage());
        }
    }


    public void setTaskFactory(Task taskFactory) {
        this.taskFactory = taskFactory;
    }
    @SuppressWarnings("unused")
    public Task getTaskFactory() {
        return taskFactory;
    }
}
