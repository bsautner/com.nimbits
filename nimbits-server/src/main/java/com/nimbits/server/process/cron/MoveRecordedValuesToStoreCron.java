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

package com.nimbits.server.process.cron;

import com.nimbits.client.model.point.Point;
import com.nimbits.server.api.ApiBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;


public class MoveRecordedValuesToStoreCron extends ApiBase  {
    /**
     *
     */
    private static final long serialVersionUID = 2L;

    protected final static Logger log = Logger.getLogger(MoveRecordedValuesToStoreCron.class.getName());

    @Override
    @SuppressWarnings("unchecked")
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        setup(req, resp);
        final Map<String, Point> points = entityService.getActivePoints();

        for (final Point point : points.values()) {
            log.info("cron task moving " + point.getKey());
            taskService.startMoveCachedValuesToStoreTask(point);
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
