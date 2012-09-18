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

package com.nimbits.server.process.cron;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.process.task.TaskFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 5:24 PM
 */
@Service("moveCron")
@Transactional
public class MoveRecordedValuesToStoreCron extends HttpServlet implements org.springframework.web.HttpRequestHandler{
    /**
     *
     */
    private static final long serialVersionUID = 1L;



    @Override
    @SuppressWarnings("unchecked")
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        final MemcacheService cacheShared = MemcacheServiceFactory.getMemcacheService();
        if (cacheShared.contains(MemCacheKey.activePoints)) {
            try {
                final Map<String, Point> points = (Map<String, Point>) cacheShared.get(MemCacheKey.activePoints);
                cacheShared.delete(MemCacheKey.activePoints); //TODO possible race condition with record value service
                for (final Point point : points.values()) {
                      TaskFactory.getInstance().startMoveCachedValuesToStoreTask(point);
                }
            } catch (InvalidValueException e) {
                cacheShared.clearAll();
            }

        }

    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
