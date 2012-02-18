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

import com.nimbits.client.model.point.*;
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
    // MemcacheService systemCache;

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        PrintWriter out = resp.getWriter();
        int count = 0;
       //todo limit to active points

        final List<Point> points = PointServiceFactory.getInstance().getAllPoints();

        StringBuilder sb = new StringBuilder();
        for (final Point point : points) {
            count++;
        //   sb.append("<p>" + point.getName() + "</p>");
            TaskFactoryLocator.getInstance().startMoveCachedValuesToStoreTask(point);
        }

        out.print("<h4> Total Points (using datastore): " + count + "</h4>");
        out.print(sb.toString());
        out.close();
    }
}
