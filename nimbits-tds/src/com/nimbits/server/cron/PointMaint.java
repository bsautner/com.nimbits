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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.cron;

import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.task.TaskFactoryLocator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PointMaint extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        PrintWriter out;


        //  final List<Point> points = PointServiceFactory.getInstance().getAllPoints();
        resp.setContentType(Const.CONTENT_TYPE_HTML);
        out = resp.getWriter();
        out.println(Const.HTML_BOOTSTRAP);
//

        int set = 0;
        int results = -1;
        int count = 0;
        while (results != 0) {
            List<Point> points = PointServiceFactory.getInstance().getAllPoints(set, set + Const.CONST_QUERY_CHUNK_SIZE);
            results = points.size();
            set += Const.CONST_QUERY_CHUNK_SIZE;

            for (final Point p : points) {
                String c;
                count++;
                if (p.isPublic()) {
                    c = "label success";
                } else {
                    c = "label";
                }


                out.println("<li><span class = \"" + c + "\">" + p.getName().getValue() + "  " + p.getDescription() + " " + p.getUUID() + "</span></li>");
                TaskFactoryLocator.getInstance().startPointMaintTask(p);
            }
        }
        out.println("<h5>" + count + " Points.");
        out.println("</body></html>");
        out.close();


    }
}
