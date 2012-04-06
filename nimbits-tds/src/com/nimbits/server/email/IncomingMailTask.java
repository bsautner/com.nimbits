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

package com.nimbits.server.email;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.point.*;
import com.nimbits.server.user.*;
import com.nimbits.server.value.*;

import javax.servlet.http.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

public class IncomingMailTask extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //  private final Map<String, Point> points = new HashMap<String, Point>();
    private static final Logger log = Logger.getLogger(IncomingMailTask.class.getName());
    private static final Pattern COMPILE = Pattern.compile(",");
    private static final Pattern PATTERN = Pattern.compile("\n");
    private static final Pattern COMPILE1 = Pattern.compile("\r");


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        final String fromAddress = req.getParameter(Parameters.fromAddress.getText());
        final String inContent = req.getParameter(Parameters.inContent.getText());

        final EmailAddress internetAddress;
        try {
            internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(fromAddress);

            final User u;

            log.info("Incoming mail post: " + internetAddress);
            u = UserTransactionFactory.getInstance().getNimbitsUser(internetAddress);

            final String content = COMPILE1.matcher(PATTERN.matcher(inContent).replaceAll("")).replaceAll("");
            final String Data[] = content.split(";");
            log.info("Incoming mail post: " + inContent);

            if (u != null) {
                if (Data.length > 0) {
                    for (String s : Data) {
                        processLine(u, s);
                    }
                }
            } else {
                log.severe("Null user for incoming mail:" + fromAddress);

            }
        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }
        //TODO add users to list of spammers

    }

    void processLine(final User u, final String s) throws NimbitsException {
        final String emailLine[] = COMPILE.split(s);
        final EntityName pointName = CommonFactoryLocator.getInstance().createName(emailLine[0], EntityType.point);

        Point e = (Point) EntityServiceFactory.getInstance().getEntityByName(u, pointName,PointEntity.class.getName());


        if (e != null) {
            sendValue(u, e, emailLine);
        }
    }

    private static void sendValue(final User u,
                                  final Point point,
                                  final String k[]) throws NimbitsException {


        long timestamp;
        Double v = 0.0;
        String note;

        if (k != null && k.length > 1) {

            try {
                v = Double.valueOf(k[1].trim());
            } catch (NumberFormatException e1) {
                log.info("Invalid mail message from: " + u.getEmail() + " " + k[0] + "," + k[1]);
            }

            if (k.length == 3) {

                try {
                    String ts = k[2].trim();
                    timestamp = Long.parseLong(ts);
                } catch (NumberFormatException e) {
                    timestamp = new Date().getTime();
                    log.info("Invalid mail message from: " + u.getEmail() + " " + k[0] + "," + k[1] + "," + k[2]);
                }
            } else {
                timestamp = new Date().getTime();
            }
            if (k.length == 4) {
                note = (k[3].trim());
            } else {
                note = "";
            }
            final Value value = ValueModelFactory.createValueModel(0.0, 0.0, v, new Date(timestamp), point.getKey(), note);
            try {
                RecordedValueServiceFactory.getInstance().recordValue(u, point, value, false);
            } catch (NimbitsException e) {
                log.severe(e.getMessage());
                if (u != null) {
                    FeedServiceFactory.getInstance().postToFeed(u, e);
                }
            }
        }


    }

}
