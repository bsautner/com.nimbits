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
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.nimbits.server.admin.logging.LogHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;



@Service("spreadsheetApi")
@Transactional
public class SpreadsheetServlet extends HttpServlet implements org.springframework.web.HttpRequestHandler {


    DocsService docsService;
    SpreadsheetService spreadsheetService;




    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    public void setup() {

        String consumerKey = "1009209848329.apps.googleusercontent.com"; //getInitParameter("consumer_key");
        String consumerSecret = "m4S1GkGguCvyFO70bxHuKNzH";///getInitParameter("consumer_secret");
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);
        docsService = new DocsService("nimbits-com");
        spreadsheetService = new SpreadsheetService("nimbits-com");


        try {
            docsService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
            spreadsheetService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
        } catch (OAuthException e) {
            LogHelper.logException(this.getClass(), e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setup();
        UserInfo user = (UserInfo) req.getSession().getAttribute("user");

        List<DocumentListEntry> docs;
        PrintWriter o = resp.getWriter();
        try {
            SpreadsheetFeed feed = createDocument(user);

            if (feed != null && feed.getEntries().size() > 0) {
                o.println(feed.getEntries().size());
                com.google.gdata.data.spreadsheet.SpreadsheetEntry entry = feed.getEntries().get(0);
                o.println("Worksheets: " + entry.getWorksheets().size());
                WorksheetEntry sheet = entry.getWorksheets().get(0);
                o.println(sheet.getTitle().getPlainText());
                // sheet.setTitle(TextConstruct.plainText("POINT1"));
                URL cellFeedUrl= sheet.getCellFeedUrl ();
                CellFeed cellFeed= spreadsheetService.getFeed (cellFeedUrl,
                        CellFeed.class);

                CellEntry cellEntry= new CellEntry (1, 1, "Timestamp");
                cellFeed.insert (cellEntry);

                cellEntry= new CellEntry (1, 2, "Value");
                cellFeed.insert (cellEntry);

                cellEntry= new CellEntry (1, 3, "Latitude");
                cellFeed.insert (cellEntry);

                cellEntry= new CellEntry (1, 4, "Longitude");
                cellFeed.insert (cellEntry);

                cellEntry= new CellEntry (1, 5, "Annotation");
                cellFeed.insert (cellEntry);

                cellEntry= new CellEntry (1, 6, "Data");
                cellFeed.insert (cellEntry);
                Random r = new Random();

                SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");
                for (int i = 2; i < 10; i++) {
                    cellEntry= new CellEntry (i, 1, dtf.format(new Date()));
                    cellFeed.insert (cellEntry);

                    cellEntry= new CellEntry (i, 2, String.valueOf(r.nextDouble()));
                    cellFeed.insert (cellEntry);


                    cellEntry= new CellEntry (i, 3, "40.283716");
                    cellFeed.insert (cellEntry);

                    cellEntry= new CellEntry (i, 4, "-75.901794");
                    cellFeed.insert (cellEntry);

                    cellEntry= new CellEntry (i, 5, "big bucks");
                    cellFeed.insert (cellEntry);


                }




                // sheet.update();


            }
            else {
                o.println("couldn't get it back");
            }
        } catch (ServiceException e) {
            o.println(e.getMessage());
            String s = ExceptionUtils.getStackTrace(e);
            o.println(s);
        }



    }



    public SpreadsheetFeed createDocument(UserInfo user) throws IOException, ServiceException {

        SpreadsheetEntry entry = new SpreadsheetEntry();
        String title = "WS " + new Date().getTime();
        entry.setTitle(TextConstruct.plainText(title));

        SpreadsheetEntry newEntry = docsService.insert(
                new URL("https://docs.google.com/feeds/default/private/full?xoauth_requestor_id="
                        + user.getEmail()),
                entry);



        SpreadsheetQuery query =
                new SpreadsheetQuery(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"));

        query.addCustomParameter(new Query.CustomParameter(
                "xoauth_requestor_id", user.getEmail()));
        query.setTitleQuery(title);
        SpreadsheetFeed results =  spreadsheetService.query(query, SpreadsheetFeed.class);


        if (!results.getEntries().isEmpty()) {



            return results;
        } else {
            return null;
        }



    }
    public void addData(UserInfo user) throws IOException, ServiceException {
//
//        SpreadsheetQuery query = new SpreadsheetQuery(new URL("https://docs.google.com/feeds/default/private/full"));
//        query.addCustomParameter(new Query.CustomParameter(
//                "xoauth_requestor_id", user.getEmail()));
//
//        query.setTitleQuery("hello s");
//        query.setTitleExact(true);
//        SpreadsheetFeed feed = docsService.query(query, SpreadsheetFeed);
//        //entry.setKind("spreadsheet");
////        SpreadsheetEntry newEntry = docsService.insert(
////                new URL("https://docs.google.com/feeds/default/private/full?xoauth_requestor_id="
////                        + user.getEmail()),
////                entry);


    }

    private List<DocumentListEntry> getDocs(UserInfo user) throws IOException, ServiceException {

        DocumentQuery query =
                new DocumentQuery(new URL("https://docs.google.com/feeds/default/private/full"));

        query.addCustomParameter(new Query.CustomParameter(
                "xoauth_requestor_id", user.getEmail()));

        DocumentListFeed results =  docsService.query(query, DocumentListFeed.class);

        if (!results.getEntries().isEmpty()) {
            return results.getEntries();
        } else {
            return null;
        }
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}



