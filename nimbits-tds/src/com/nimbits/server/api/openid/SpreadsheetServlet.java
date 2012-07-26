/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.api.openid;


import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.Query;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.docs.DocsService;

import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;
import com.nimbits.server.admin.logging.LogHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 7/25/12
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class SpreadsheetServlet extends HttpServlet {
    //  CalendarService calendarService;
    DocsService docsService;
    final Logger log = Logger.getLogger(SpreadsheetServlet.class.getName());
    String initError;

    /**
     * Initializes the calendar service using OAuth client key & secret from web.xml.  Please note
     * that in production applications the consumer secret should be treated the same as sensitive
     * data that should be protected from casual browsing, as would any password or encryption key.
     *
     * @param config Servlet config
     * @throws ServletException if unable to initialize
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }


    public void setup() {
        log.info("init");
        String consumerKey = getInitParameter("consumer_key");
        String consumerSecret = getInitParameter("consumer_secret");
        log.info(consumerKey);
        log.info(consumerSecret);
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);

        initError = "";
        // calendarService = new CalendarService("marketplace-nextmeeting");
        docsService = new DocsService("marketplace-nextmeeting");

        log.info("null" + (docsService == null));
        try {
//            calendarService.setOAuthCredentials(oauthParameters,
//                    new OAuthHmacSha1Signer());

            docsService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
            //docsService.setAuthSubToken(oauthParameters.getOAuthToken());
        } catch (OAuthException e) {
            initError = "Unable to initialize service: "
                    + e.getMessage();
            LogHelper.logException(this.getClass(), e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setup();
        UserInfo user = (UserInfo) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("?errorString=Invalid user information.");
        } else if (!initError.isEmpty()) {
            resp.sendRedirect("?errorString=" + initError);
        } else {
            // try {
            // List<CalendarEventEntry> entry = null;
            List<DocumentListEntry> docs = null;
            PrintWriter o = resp.getWriter();
            try {
                // entry = nextEvent(user);
                docs = getDocs(user);
                if (docs != null) {
                    for (DocumentListEntry er : docs) {
                        o.println(er.getTitle().getPlainText());
                    }
                }
                else {
                    o.println("no results");
                }
            } catch (ServiceException e) {
                o.println(e.getMessage());
                String s = ExceptionUtils.getStackTrace(e);
                o.println(s);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

//
//            } catch (Exception e) {
//                resp.sendRedirect("?errorString=Unable to fetch next calendar event: "
//                        + e.getMessage());
//            }
//            req.getRequestDispatcher("/WEB-INF/jsp/meeting.jsp").forward(req,
//                    resp);
        }
    }

    private List<DocumentListEntry> getDocs(UserInfo user) throws IOException, ServiceException {

        log.info("null" + (docsService == null));


        // DocumentQuery query = new DocumentQuery(feedUrl);
        DocumentQuery query =
                new DocumentQuery(new URL("https://docs.google.com/feeds/default/private/full"));

        // query.setMaxResults(5);

        query.addCustomParameter(new Query.CustomParameter(
                "xoauth_requestor_id", user.getEmail()));
        // Get Everything

        // DocumentListFeed tempFeed = docsService.getFeed(query, DocumentListFeed.class);


        DocumentListFeed results =  docsService.query(query, DocumentListFeed.class);

        if (!results.getEntries().isEmpty()) {
            return results.getEntries();
        } else {
            return null;
        }
    }

    /**
     * Handles the actual query for the Google Calendar API.  This is a simple query
     * that checks the user's default calendar for the next event (limit 1, sort by start
     * time).  It also uses 2-legged OAuth (xoauth_requestor_id) to impersonate the user
     * rather than using an individual access token.
     *
     * @param user User to request data for
     * @return Next calendar event, null if none found
     * @throws IOException If error retrieving data
     * @throws com.google.gdata.util.ServiceException If error retrieving data
     */
//    private List<CalendarEventEntry> nextEvent(UserInfo user)
//            throws IOException, ServiceException {
//        URL feedUrl = new URL(
//                "https://www.google.com/calendar/feeds/default/private/full");
//        CalendarQuery query = new CalendarQuery(feedUrl);
//        query.setMinimumStartTime(DateTime.now());
//        query.setMaxResults(5);
//        query.addCustomParameter(new Query.CustomParameter("orderby",
//                "starttime"));
//        query.addCustomParameter(new Query.CustomParameter("sortorder", "a"));
//        query.addCustomParameter(new Query.CustomParameter("singleevents",
//                "true"));
//        query.addCustomParameter(new Query.CustomParameter(
//                "xoauth_requestor_id", user.getEmail()));
//
//        CalendarEventFeed results = calendarService.query(query,
//                CalendarEventFeed.class);
//        if (!results.getEntries().isEmpty()) {
//            return results.getEntries();
//        } else {
//            return null;
//        }
//    }
}



