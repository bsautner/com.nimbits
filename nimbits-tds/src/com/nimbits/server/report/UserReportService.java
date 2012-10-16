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

package com.nimbits.server.report;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.ApiServlet;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/17/11
 * Time: 4:21 PM
 */
@Service("userReportService")
public class UserReportService  extends ApiServlet implements org.springframework.web.HttpRequestHandler {

    private static final int INT = 60;



    @Override
    public void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws IOException {


        final PrintWriter out = resp.getWriter();
        Map<EmailAddress, User> map = quotaManager.getUserStatusGrid();



            out.println("<p>Total number of users: " + map.size() + "</p>");
            out.println("<table border = 1>");
            out.println("<TR>");
            out.println("<TD>" + "User" + "</TD>");
            out.println("<TD>" + "Last Logged In" + "</TD>");
            out.println("<TD>" + "Date Created" + "</TD>");
            out.println("<TD>" + "API Calls" + "</TD>");
            out.println("</TR>");

            for (final User u : map.values()) {
                boolean returnedUser = !(u.getLastLoggedIn().getTime() - u.getDateCreated().getTime() > 1000 * INT);

                if (returnedUser) {
                    out.println("<TR  bgcolor=\"#006633\">");
                } else {
                    out.println("<TR  bgcolor=\"#CCCC99\">");
                }
                try {
                    out.println("<TD>" + u.getEmail().getValue() + "</TD>");
                } catch (NimbitsException e) {
                    out.println(e.getMessage());
                }

                out.println("<TD>" + u.getLastLoggedIn() + "</TD>");
                out.println("<TD>" + u.getDateCreated() + "</TD>");
                out.println("<TD>" + u.getApiCount() + "</TD>");
                out.println("</TR>");

            }

            out.println("</table>");
        out.close();


    }

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (isPost(req)) {

            doPost(req, resp);
        }
        else {
            doGet(req, resp);
        }

    }
}
