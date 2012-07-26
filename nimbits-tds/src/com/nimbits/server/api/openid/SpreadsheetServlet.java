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
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
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
import java.net.MalformedURLException;
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

    DocsService docsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    public void setup() {

        String consumerKey = getInitParameter("consumer_key");
        String consumerSecret = getInitParameter("consumer_secret");
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);
        docsService = new DocsService("nimbits-com");


        try {
            docsService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

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
            docs = getDocs(user);
            if (docs != null) {
                for (DocumentListEntry er : docs) {
                    o.println(er.getTitle().getPlainText());
                }
            }
            else {
                o.println("no results");
            }
            createDocument(user);
        } catch (ServiceException e) {
            o.println(e.getMessage());
            String s = ExceptionUtils.getStackTrace(e);
            o.println(s);
        }



    }

    public void createDocument(UserInfo user) throws IOException, ServiceException {



        SpreadsheetEntry entry = new SpreadsheetEntry();
        entry.setTitle(TextConstruct.plainText("hello s1"));
         entry.setContent(TextConstruct.plainText("{f:1, d:2}"));
        //entry.setKind("spreadsheet");
        SpreadsheetEntry newEntry = docsService.insert(
                new URL("https://docs.google.com/feeds/default/private/full?xoauth_requestor_id="
                        + user.getEmail()),
                entry);





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

}



