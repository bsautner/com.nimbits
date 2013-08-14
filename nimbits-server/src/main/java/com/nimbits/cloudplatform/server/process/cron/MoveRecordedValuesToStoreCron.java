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

package com.nimbits.cloudplatform.server.process.cron;

import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 5:24 PM
 */
@Service("moveCron")

public class MoveRecordedValuesToStoreCron extends HttpServlet implements org.springframework.web.HttpRequestHandler{
    /**
     *
     */
    private static final long serialVersionUID = 2L;

    protected final static Logger log = Logger.getLogger(MoveRecordedValuesToStoreCron.class.getName());


    @Override
    @SuppressWarnings("unchecked")
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        final Map<String, Point> points = EntityServiceImpl.getActivePoints();

        for (final Point point : points.values()) {
            log.info("cron task moving " + point.getKey());
            TaskImpl.startMoveCachedValuesToStoreTask(point);
        }

    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
