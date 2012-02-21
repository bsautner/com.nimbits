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

import com.nimbits.client.exception.*;
import com.nimbits.server.task.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.logging.*;


public class BatchService extends HttpServlet {

    private static final Logger log = Logger.getLogger(BatchService.class.getName());

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        // delete me context.addTrace("Batch Service Post " + req.getQueryString());
        log.info("Batch Service Post " + req.getQueryString());
        try {
            TaskFactoryLocator.getInstance().startProcessBatchTask(req, resp);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }
        try {
            resp.flushBuffer();
            resp.setContentLength(0);
        } catch (IOException ignored) {

        }

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        // delete me UserContext context = UserContextFactory.createUserContext(BatchService.class.getName());
        // delete me context.addTrace("Batch Service Get " + req.getQueryString());
        log.info("Batch Service Get " + req.getQueryString());
        try {
            TaskFactoryLocator.getInstance().startProcessBatchTask(req, resp);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }
        try {
            resp.flushBuffer();
            resp.setContentLength(0);
        } catch (IOException ignored) {

        }
    }


}
