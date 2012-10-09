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

package com.nimbits.server.external.google.drive;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gdata.client.Query;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.docs.DriveService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.transactions.service.user.UserServerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/3/12
 * Time: 3:22 PM
 */
@Service("driveService")
@Transactional
public class DriveServiceImpl extends RemoteServiceServlet implements
        DriveService, RequestCallback {
    private static final int MAX_COLS = 6;
    static final Logger log = Logger.getLogger(DriveServiceImpl.class.getName());
    private static final String NIMBITS_COM = "nimbits-com";
    private static final String DOC_ID = "docId";
    private static final String FILE_NAME = "fileName";
    private UserServerService userService;
    private ValueService valueService;
    private DocsService docService;

    private final static String consumerKey = "1009209848329.apps.googleusercontent.com";
    private final static String consumerSecret = "m4S1GkGguCvyFO70bxHuKNzH";


    private final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();


    @Override
    public String createGoogleDoc(final Entity entity, final String fileName) throws NimbitsException {


        final User user = userService.getHttpRequestUser(
                this.getThreadLocalRequest());




        docService = new DocsService(NIMBITS_COM);
//        String consumerKey = "1009209848329.apps.googleusercontent.com";
//        String consumerSecret = "m4S1GkGguCvyFO70bxHuKNzH";
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);


        try {
            docService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
            SpreadsheetEntry entry = new SpreadsheetEntry();

            entry.setTitle(TextConstruct.plainText(fileName));

            SpreadsheetEntry newEntry = docService.insert(
                    new URL("https://docs.google.com/feeds/default/private/full?xoauth_requestor_id="
                            + user.getEmail()),
                    entry);
            final String key = user.getKey() + MemCacheKey.docService;
            cache.put(key + DOC_ID, newEntry.getDocId());
            cache.put(key + FILE_NAME, fileName);

            return newEntry.getWorksheetFeedUrl().toString();
        } catch (OAuthException e) {
            throw new NimbitsException(e);
        } catch (ServiceException e) {
            throw new NimbitsException(e);
        } catch (MalformedURLException e) {
            throw new NimbitsException(e);
        } catch (IOException e) {
            throw new NimbitsException(e);
        }


    }


    @Override
    public void setSpreadsheetSize(final Entity entity, final int count, String title) throws NimbitsException {

        final User user = userService.getHttpRequestUser(
                this.getThreadLocalRequest());
        SpreadsheetService spreadsheetService;

        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);
        spreadsheetService = new SpreadsheetService(NIMBITS_COM);

        try {
            spreadsheetService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

            SpreadsheetQuery query = new SpreadsheetQuery(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"));
            query.addCustomParameter(new Query.CustomParameter("xoauth_requestor_id", user.getEmail().getValue()));

            query.setTitleQuery(title);
            SpreadsheetFeed feed =  spreadsheetService.query(query, SpreadsheetFeed.class);
            if (feed != null && ! feed.getEntries().isEmpty()) {
                com.google.gdata.data.spreadsheet.SpreadsheetEntry wsEntry = feed.getEntries().get(0);
                WorksheetEntry sheet = wsEntry.getWorksheets().get(0);

                sheet.setRowCount(count+ 2 + Const.CONST_QUERY_CHUNK_SIZE);
                sheet.setColCount(MAX_COLS);
                sheet.update();
            }

        } catch (MalformedURLException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);

        } catch (ServiceException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        } catch (IOException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        } catch (OAuthException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        }


    }


    @Override
    public void addSpreadsheetHeader(final Entity entity, final String title) throws NimbitsException {
        final User user = userService.getHttpRequestUser(
                this.getThreadLocalRequest());


        SpreadsheetService spreadsheetService;

        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);
        spreadsheetService = new SpreadsheetService(NIMBITS_COM);





        try {
            spreadsheetService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
            SpreadsheetQuery query = new SpreadsheetQuery(new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full"));
            query.addCustomParameter(new Query.CustomParameter("xoauth_requestor_id", user.getEmail().getValue()));

            query.setTitleQuery(title);
            SpreadsheetFeed feed =  spreadsheetService.query(query, SpreadsheetFeed.class);
            if (feed != null && ! feed.getEntries().isEmpty()) {
                com.google.gdata.data.spreadsheet.SpreadsheetEntry wsEntry = feed.getEntries().get(0);
                WorksheetEntry sheet = wsEntry.getWorksheets().get(0);
                URL cellFeedUrl= sheet.getCellFeedUrl ();
                CellFeed cellFeed= spreadsheetService.getFeed (cellFeedUrl, CellFeed.class);
                CellEntry cellEntry;

                cellEntry= new CellEntry (1, 1, "Timestamp");
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

            }



        } catch (MalformedURLException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);

        } catch (ServiceException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        } catch (IOException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        } catch (OAuthException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        }


    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onError(Request request, Throwable exception) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUserService(UserServerService userService) {
        this.userService = userService;
    }

    public UserServerService getUserService() {
        return userService;
    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }

    public void setDocService(DocsService docService) {
        this.docService = docService;
    }

    public DocsService getDocService() {
        return docService;
    }

    private static class CellAddress {
        public final int row;
        public final int col;
        public final String idString;

        /**
         * Constructs a CellAddress representing the specified {@code row} and
         * {@code col}.  The idString will be set in 'RnCn' notation.
         */
        public CellAddress(int row, int col) {
            this.row = row;
            this.col = col;
            this.idString = String.format("R%sC%s", row, col);
        }
    }


    @Override
    public int dumpValues(final Entity entity, int section) throws NimbitsException {

        final User user = userService.getHttpRequestUser(
                this.getThreadLocalRequest());

        log.severe("getting preloaded section" + section);
        List<Value> values =valueService.getPreload(entity, section);


        SpreadsheetService spreadsheetService;

        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(consumerKey);
        oauthParameters.setOAuthConsumerSecret(consumerSecret);
        spreadsheetService = new SpreadsheetService(NIMBITS_COM);
        spreadsheetService.setProtocolVersion(SpreadsheetService.Versions.V3);


        try {
            spreadsheetService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

            final String cKey = user.getKey() + MemCacheKey.docService;
            String key= (String) cache.get(cKey + DOC_ID);



            FeedURLFactory urlFactory = FeedURLFactory.getDefault();
            URL cellFeedUrl = urlFactory.getCellFeedUrl(key, "od6", "private", "full");


            CellQuery q = new CellQuery(cellFeedUrl);
            //CellQuery q = new CellQuery(worksheet.getCellFeedUrl());
            log.info("loading section" + section);
            log.info("Values: " + values.size());
            q.setMinimumRow(section + 2);
            q.setMaximumRow(section + 2 + values.size());
            q.setMinimumCol(1);
            q.setMaximumCol(MAX_COLS);
            q.setReturnEmpty(true);
            q.addCustomParameter(new Query.CustomParameter("xoauth_requestor_id", user.getEmail().getValue()));
            CellFeed cellFeed = spreadsheetService.query(q, CellFeed.class);

            CellFeed batchRequestFeed = new CellFeed();
            SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");
            // set values for each cell
            int currentCellEntry=0;
            for (Value value1 : values) {


                String timestamp = dtf.format(value1.getTimestamp());
                CellEntry entry = new CellEntry(cellFeed.getEntries().get(currentCellEntry));
                entry.changeInputValueLocal(timestamp);
                BatchUtils.setBatchId(entry, (new Integer(currentCellEntry)).toString());
                BatchUtils.setBatchOperationType(entry, BatchOperationType.UPDATE);
                batchRequestFeed.getEntries().add(entry);
                currentCellEntry++;


                String value = String.valueOf(value1.getDoubleValue());
                entry = new CellEntry(cellFeed.getEntries().get(currentCellEntry));
                entry.changeInputValueLocal(value);
                BatchUtils.setBatchId(entry, (new Integer(currentCellEntry)).toString());
                BatchUtils.setBatchOperationType(entry, BatchOperationType.UPDATE);
                batchRequestFeed.getEntries().add(entry);
                currentCellEntry++;


                String lat =  String.valueOf(value1.getLocation().getLat());
                entry = new CellEntry(cellFeed.getEntries().get(currentCellEntry));
                entry.changeInputValueLocal(lat);
                BatchUtils.setBatchId(entry, (new Integer(currentCellEntry)).toString());
                BatchUtils.setBatchOperationType(entry, BatchOperationType.UPDATE);
                batchRequestFeed.getEntries().add(entry);
                currentCellEntry++;


                String lng =  String.valueOf(value1.getLocation().getLng());
                entry = new CellEntry(cellFeed.getEntries().get(currentCellEntry));
                entry.changeInputValueLocal(lng);
                BatchUtils.setBatchId(entry, (new Integer(currentCellEntry)).toString());
                BatchUtils.setBatchOperationType(entry, BatchOperationType.UPDATE);
                batchRequestFeed.getEntries().add(entry);
                currentCellEntry++;


                String note = value1.getNote();
                entry = new CellEntry(cellFeed.getEntries().get(currentCellEntry));
                entry.changeInputValueLocal(note);
                BatchUtils.setBatchId(entry, (new Integer(currentCellEntry)).toString());
                BatchUtils.setBatchOperationType(entry, BatchOperationType.UPDATE);
                batchRequestFeed.getEntries().add(entry);
                currentCellEntry++;

                String data = value1.getData().getContent();
                entry = new CellEntry(cellFeed.getEntries().get(currentCellEntry));
                entry.changeInputValueLocal(data);
                BatchUtils.setBatchId(entry, (new Integer(currentCellEntry)).toString());
                BatchUtils.setBatchOperationType(entry, BatchOperationType.UPDATE);
                batchRequestFeed.getEntries().add(entry);
                currentCellEntry++;


            }


            // upload cells
            Link batchLink = cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
            spreadsheetService.setHeader("If-Match", "*");

            CellFeed batchResponse = spreadsheetService.batch(new URL(batchLink.getHref() ), batchRequestFeed);
            spreadsheetService.setHeader("If-Match", null);
            for (CellEntry entry : batchResponse.getEntries()) {
                if (!BatchUtils.isSuccess(entry)) {

                    BatchStatus status = BatchUtils.getBatchStatus(entry);
                    throw new NimbitsException(BatchUtils.getBatchId(entry) + " " + status.getReason() + " " + status.getContent());
                }
            }
            return values.size();
        } catch (IOException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        } catch (ServiceException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        } catch (OAuthException e) {
            LogHelper.logException(this.getClass(), e);
            throw new NimbitsException(e);
        }
    }



    public static Map<String, CellEntry> getCellEntryMap(
            SpreadsheetService ssSvc, URL cellFeedUrl, List<CellAddress> cellAddrs)
            throws IOException, ServiceException {
        CellFeed batchRequest = new CellFeed();

        for (CellAddress cellId : cellAddrs) {
            CellEntry batchEntry = new CellEntry(cellId.row, cellId.col, cellId.idString);
            batchEntry.setId(String.format("%s/%s", cellFeedUrl.toString(), cellId.idString));
            BatchUtils.setBatchId(batchEntry, cellId.idString);
            BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.QUERY);

            batchRequest.getEntries().add(batchEntry);
        }


        CellFeed cellFeed = ssSvc.getFeed(cellFeedUrl, CellFeed.class);
        CellFeed queryBatchResponse =
                ssSvc.batch(new URL(cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM).getHref()),
                        batchRequest);

        Map<String, CellEntry> cellEntryMap = new HashMap<String, CellEntry>(cellAddrs.size());
        for (CellEntry entry : queryBatchResponse.getEntries()) {
            cellEntryMap.put(BatchUtils.getBatchId(entry), entry);
//            System.out.printf("batch %s {CellEntry: id=%s editLink=%s inputValue=%s\n",
//                    BatchUtils.getBatchId(entry), entry.getId(), entry.getEditLink().getHref(),
//                    entry.getCell().getInputValue());
        }

        return cellEntryMap;
    }


}
