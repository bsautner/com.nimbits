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

package com.nimbits.server.cron;

import com.google.appengine.api.memcache.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.memcache.*;
import com.nimbits.server.point.*;
import com.nimbits.server.task.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 5:24 PM
 */
public class MoveRecordedValuesToStoreCron extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;



    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        final MemcacheService cacheShared = MemcacheServiceFactory.getMemcacheService();
        if (cacheShared.contains(MemCacheKey.activePoints)) {
            final Map<String, Point> points = (Map<String, Point>) cacheShared.get(MemCacheKey.activePoints);
            cacheShared.delete(MemCacheKey.activePoints); //TODO possible race condition with record value service
            for (final Point point : points.values()) {
                  TaskFactoryLocator.getInstance().startMoveCachedValuesToStoreTask(point);
            }

        }

    }
}
