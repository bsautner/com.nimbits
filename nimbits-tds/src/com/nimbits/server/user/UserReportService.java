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

package com.nimbits.server.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.counter.CounterFactory;
import com.nimbits.server.dao.counter.ShardedCounter;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;
import com.nimbits.shared.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/17/11
 * Time: 4:21 PM
 */
public class UserReportService extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String c = req.getParameter("count");
        String email = req.getParameter("email");
        final PrintWriter out = resp.getWriter();
        int count = 100;

        if (! Utils.isEmptyString(c)) {
            count = Integer.valueOf(c);
        }


        if (Utils.isEmptyString(email)) {

            final List<User> users = UserTransactionFactory.getInstance().getAllUsers("lastLoggedIn desc", count);


            final User me;
            try {
                me = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            } catch (NimbitsException e) {
                return;
            }
            final PointName pointName = CommonFactoryLocator.getInstance().createPointName("User Count");

            // Point p = PointServiceFactory.getInstance().getPointByName(me, pointName);
            final Value value = ValueModelFactory.createValueModel((double) users.size());

            try {
                RecordedValueServiceFactory.getInstance().recordValue(me, pointName, value);
            } catch (NimbitsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            out.println("<p>Total number of users: " + users.size() + "</p>");
            out.println("<table border = 1>");
            out.println("<TR>");
            out.println("<TD>" + "User" + "</TD>");
            out.println("<TD>" + "Last Logged In" + "</TD>");
            out.println("<TD>" + "Date Created" + "</TD>");
            out.println("<TD>" + "API Calls" + "</TD>");
            out.println("</TR>");

            for (final User u : users) {
                boolean returnedUser = !(u.getLastLoggedIn().getTime() - u.getDateCreated().getTime() > 1000 * 60);

                if (returnedUser) {
                    out.println("<TR  bgcolor=\"#006633\">");
                } else {
                    out.println("<TR  bgcolor=\"#CCCC99\">");
                }

                out.println("<TD>" + u.getEmail() + "</TD>");
                out.println("<TD>" + u.getLastLoggedIn() + "</TD>");
                out.println("<TD>" + u.getDateCreated() + "</TD>");

                ShardedCounter counter = getOrCreateCounter(u.getEmail());
                out.println("<TD>" +counter.getCount() + "</TD>");
                out.println("</TR>");
            }

            out.println("</table>");
        }
        else {
            EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
            out.println("<h5>" + getOrCreateCounter(emailAddress).getCount() + "</h5>");

            out.println();
        }


        out.close();
    }
    private ShardedCounter getOrCreateCounter(EmailAddress email) {
        CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getCounter(email.getValue());
        if (counter == null) {
            counter = factory.createCounter(email.getValue());
            counter.addShard();

        }
        return counter;
    }
}
