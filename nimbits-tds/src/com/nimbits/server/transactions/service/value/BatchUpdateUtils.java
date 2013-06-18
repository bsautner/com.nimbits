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

package com.nimbits.server.transactions.service.value;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.data.Link;
import com.google.gdata.data.spreadsheet.CellFeed;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 7/29/12
 * Time: 12:47 PM
 */
public class BatchUpdateUtils {

    public static URL getBatchUpdateFeedUrl(CellFeed cellFeed) throws MalformedURLException {
        return new URL(cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM).getHref());
    }

    public static URL getCellFeedUrl(String key) throws MalformedURLException {
        FeedURLFactory urlFactory = FeedURLFactory.getDefault();
        URL cellFeedUrl = urlFactory.getCellFeedUrl(key, "od6", "private", "full");
        return cellFeedUrl;
    }

}