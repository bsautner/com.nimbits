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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DailyReport extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        //
        //
        //
        // //String m ="";
        // List<DataPoint> points;
        // //RecordedValue v ;
        // Query pq;
        //
        // int i = 0;
        //
        //
        // pq = pm.newQuery(DataPoint.class);
        //
        // points = (List<DataPoint>) pq.execute( );
        //
        // for (DataPoint p : points)
        //
        // {
        // if (p.getValue() != null)
        // {
        // if (p.getValue().equals("PointQuota") ||
        // p.getValue().equals("EmailQuota"))
        // {
        // p.setSystemPoint(true);
        // }
        //
        // }
        // else
        // {
        // i++;
        // }
        //
        // }
        //
        // email.sendEmail(Global.AdminEmail, "" + points.size() + " " + i,
        // "Daily Report");
        // pm.close();

        // try {
        //
        //
        // List<NimbitsUser> users;
        // q = pm.newQuery(NimbitsUser.class);
        // users = (List<NimbitsUser>) q.execute();
        //
        // m = "<table border=1>";
        // m += "<TR>";
        //
        // m += "<TD>" + "email" + "</TD>";
        // m += "<TD>" + "Point Count" + "</TD>";
        // m += "</TR>";
        //
        //
        //
        //
        //
        // // email.sendEmail(Global.AdminEmail, "Started Report",
        // "Daily Report");
        // for ( NimbitsUser n : users)
        // {
        //
        // pq = pm.newQuery(DataPoint.class, "userFK == k");
        // pq.declareParameters("Long k");

        // points = (List<DataPoint>) pq.execute(n.getId());
        //
        //
        //
        // m += "<TR>";
        //
        // m += "<TD>" + n.getUser().getValue() + "</TD>";
        // m += "<TD>" + points.size() + "</TD>";
        //
        // m += "<TD>";
        //
        // m += "<table border=1>";
        // for (DataPoint p : points)
        //
        // {
        //
        //
        //
        // // {
        // // m += "<TR>";
        // // m += "<TD>" + p.getValue() + "</TD>";
        // //
        // //// if (p.getValue().equals("PointQuota"))
        // //// {
        // //// ds.recordSystemValue(p, new Date(), 12);
        // //// }
        // // v=ds.getCurrentValue(p);
        // // if (v != null)
        // // {
        // // m += "<TD>" + v.getTimestamp() + "</TD>";
        // // m += "<TD>" + v.getValue() + "</TD>";
        // //
        // // }
        // // m += "</TR>";
        // }
        // points = null;
        //
        // m += "</table>";
        //
        //
        // m += "</TD>";
        // m += "</TR>";
        //
        // }
        //
        // m += "</table>";

        //
        // email.sendEmail(Global.AdminEmail, m, "Daily Report");
        // }
        //
        // catch (Exception ex) {
        // ex.printStackTrace();
        // email.sendEmail(Global.AdminEmail, ex.getM(), "Daily Report");
        // // Logging.LogException(ex);
        // }
        // finally
        // {
        // pm.close();
        //
        // }

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//		doGet(req, resp);
//
//		PrintWriter out = null;
//		out = resp.getWriter();

//		List<DataPoint> points = null;
//		// ArrayList<DataPoint> retObj = new ArrayList<DataPoint>();
//		Transaction tx = null;
//		tx = pm.currentTransaction();
//
//
//			Query q = pm.newQuery(DataPoint.class);
//			q.declareImports("import java.util.Date");
//			q.setRange(0, 100);
//			q.setOrdering("LastChecked ascending");
//			points = (List<DataPoint>) q.execute();
//			boolean done;
//
//			for (DataPoint p : points) {
//
//			}
//

    }

}
