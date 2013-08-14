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

package com.nimbits.cloudplatform.server.api;


import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.server.transactions.value.ValueMemCache;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@Service("hotApi")
public class HotApi extends ApiServlet implements org.springframework.web.HttpRequestHandler {


    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final PrintWriter out = resp.getWriter();


        try {
           // doInit(req, resp, ExportType.json);
            resp.setContentType("text/html; charset=UTF-8");
            Set<Point> retSet = ValueMemCache.getHotlist();
            //  String json = GsonFactory.getInstance().toJson(retSet);

            out.println("<HTML>");
            out.println("<HEAD>");

            out.println("<link rel=\"stylesheet\" href=\"http://cloud.nimbits.com/bootstrap/css/bootstrap.css\">");
            out.println("<meta http-equiv=\"refresh\" content=\"60\">");
            out.println("</HEAD>");

            out.println("<P class=\"text-info\">Active data points on the public cloud:</p>");
            
            out.println("<table class=\"table table-condensed\" style=\"font-size : 77%\">");

            for (Point p : retSet) {
                String status = "primary";
                String badge = "primary";
                AlertType alertType = ValueTransaction.getAlertType(p, p.getValue());
                switch (alertType) {

                    case LowAlert:
                        status = "info";
                        badge = "info";
                        break;
                    case HighAlert:
                        status ="error";
                        badge="important";
                        break;
                    case IdleAlert:
                        status ="warning";
                        badge="warning";
                        break;
                    case OK:
                        status = "success";
                        badge="success";
                        break;
                }
                out.println("<TR><TD><P class=\"text-" + status + "\">" +
                        p.getName().getValue()
                        + "</p></TD><TD>" +
                        "<a href=\"http://cloud.nimbits.com/report.html?uuid="+ p.getUUID() + "\" class=\"badge badge-" + badge +"\" "
                        + "target=_blank style=\"font-size : 77%\">" +
                        + p.getValue().getDoubleValue()
                        + "</a>" +
                                "</TD></TR>");
            }
            // out.print(json);
            out.println("</table>");


            out.println("</HTML>");
        }
        finally {
            out.close();
        }



    }


}
