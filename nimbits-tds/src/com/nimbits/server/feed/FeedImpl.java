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

package com.nimbits.server.feed;

import com.google.gson.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.feed.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.feed.*;
import com.nimbits.server.common.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.user.*;
import com.nimbits.server.value.*;
import org.apache.commons.lang3.exception.*;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:02 PM
 */
public class FeedImpl extends RemoteServiceServlet implements Feed {

    private static final int MAX_LENGTH = 1024;
    private static final int SIZE = 1024;
    private static final int LENGTH = 200;
    private static final int OFFSET = 500;


    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return UserServiceFactory.getServerInstance().getAnonUser();
        }
    }

    @Override
    public void postToFeed(final User user, final Entity entity, final Point originalPoint, final Value value, final FeedType type) throws NimbitsException {
        final Point point = getFeedPoint(user);
        if (point != null) {
            final FeedValue feedValue = new FeedValueModel(valueToHtml(entity, originalPoint, value), value.getData(), type);
            final String json = GsonFactory.getSimpleInstance().toJson(feedValue);
            final Value v = ValueModelFactory.createValueModel(value, json);
            RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);
        }
    }

    @Override
    public void postToFeed(final User user, final Throwable ex) {
        LogHelper.logException(this.getClass(), ex);
        try {
            postToFeed(user, ExceptionUtils.getStackTrace(ex), FeedType.error);


        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }

    }

    @Override
    public void postToFeed(final User user, final String message, final FeedType type) throws NimbitsException {
        final Point point = getFeedPoint(user);

        if (point != null)  {
            final String shortened = message.length() > LENGTH ? message.substring(0, LENGTH) : message;

            final String finalMessage;
            try {
                finalMessage = generatePostToFeedHtml(shortened, message, type);
            } catch (UnsupportedEncodingException e) {
                throw new NimbitsException(e);
            }

            final FeedValue feedValue = new FeedValueModel(finalMessage, "", type);
            final String json = GsonFactory.getSimpleInstance().toJson(feedValue);
            final Value value = ValueModelFactory.createValueModel(0.0, 0.0, Const.CONST_IGNORED_NUMBER_VALUE,
                    new Date(),"", json);
            final Value v = ValueModelFactory.createValueModel(value, json);

            RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);

        }

    }

    private String  generatePostToFeedHtml(final String shortMessage, final String originalMessage, final FeedType type) throws UnsupportedEncodingException {
        final StringBuilder sb = new StringBuilder(MAX_LENGTH + OFFSET) ;
        final String start ="<p style=\"white-space: normal;width:150px\"><img style=\"float:left;\" ";
        switch (type) {

            case error:
                sb.append(start)
                        .append("src=\"")
                        .append(ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/symbol-error.png\" width=\"35\" height=\"35\">");

                break;
            case system:
                sb.append(start)
                        .append("src=\"")
                        .append(ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/logo.png\"  width=\"40\" height=\"40\">");
                break;
            case info:
                sb.append(start).append("src=\"")
                        .append(ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/info.png\" width=\"35\" height=\"35\">");
                break;
            case data:
                sb.append(start).append("src=\"")
                        .append(ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/point_ok.png\" width=\"40\" height=\"40\">");
                break;
            default:
                sb.append(start).append("src=\"")
                        .append(ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/logo.png\" width=\"40\" height=\"40\">");
        }

        final String shortenedOriginal = originalMessage.length() > MAX_LENGTH ? originalMessage.substring(0, MAX_LENGTH) : originalMessage;

        sb.append("<a href=\"#\" onclick=\"window.open('feed.html?content=")
                .append(URLEncoder.encode(shortenedOriginal, Const.CONST_ENCODING) )
                .append("');\">")
                .append("<span>")
                .append(new Date())
                .append("</span>")
                .append("<br /></a>")
                .append(shortMessage)
                .append("</p>");
        return sb.toString();
    }



    private String valueToHtml(final Entity entity, final Entity point, final Value value) {
        final StringBuilder sb = new StringBuilder(SIZE);
        if (! (Double.compare(value.getDoubleValue(), Const.CONST_IGNORED_NUMBER_VALUE) == 0)) {
            sb.append("<img style=\"float:left\" src=\"")
                    .append(ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()));


            switch (value.getAlertState()) {

                case LowAlert:
                    sb.append("/resources/images/point_low.png\">");
                    break;
                case HighAlert:
                    sb.append("/resources/images/point_high.png\">");
                    break;
                case IdleAlert:
                    sb.append("/resources/images/point_idle.png\">");
                    break;
                case OK:
                    sb.append("/resources/images/point_ok.png\">");
                    break;
            }
        }

        if (entity != null && point != null) {

            sb.append("&nbsp;");

            if (! (Double.compare(value.getDoubleValue(), Const.CONST_IGNORED_NUMBER_VALUE) == 0)) {
                sb.append("Alert&nbsp;Status:")
                        .append(value.getAlertState().name());
                sb.append("<br>Value:")
                        .append(value.getDoubleValue());
            }


            if (! Utils.isEmptyString(value.getNote())) {
                sb.append("<br>Note:").append(value.getNote());
            }


            sb.append("<a href=\"#\" onclick=\"window.open('report.html?uuid=")
                    .append(point.getKey())
                    .append("', 'Report',")
                    .append("'height=800,width=800,toolbar=0,status=0,location=0' );\" >")
                    .append("&nbsp;[more]</a>");

        }





        return sb.toString();
    }

    private Point getFeedPoint(final User user) throws NimbitsException {
        final Point point;
        final Map<String, Entity> map =  EntityServiceFactory.getInstance().getEntityMap(user, EntityType.feed, 1);

        if (map.isEmpty()) {
            point = createFeedPoint(user);
        }
        else {
            return (Point) map.values().iterator().next();

        }
        return point;
    }

    @Override
    public List<FeedValue> getFeed(final int count, final String relationshipEntityKey) throws NimbitsException {

        final User loggedInUser = getUser();
        final User feedUser = getFeedUser(relationshipEntityKey, loggedInUser);

        if (feedUser != null) {


            final Point point = getFeedPoint(feedUser);
            if (point == null) {
                return new ArrayList<FeedValue>(0);
            }
            else {
                final List<Value> values = RecordedValueServiceFactory.getInstance().getTopDataSeries(point, count, new Date());
                final List<FeedValue> retObj = new ArrayList<FeedValue>(values.size());

                for (final Value v : values) {
                    if (! Utils.isEmptyString(v.getData())) {
                        try {
                            retObj.add(
                                    GsonFactory.getInstance().fromJson(v.getData(),
                                            FeedValueModel.class)
                            );
                        } catch (JsonSyntaxException ignored) {

                        }
                    }
                }
                return retObj;
            }
        }
        else
        {
            return new ArrayList<FeedValue>(0);
        }
    }

    private static User getFeedUser(final String relationshipEntityKey, final User loggedInUser) throws NimbitsException {

       // if (loggedInUser != null && loggedInUser.getKey().equals(relationshipEntityKey)) {

            return loggedInUser;
//        }
//        else {
//            final Relationship r = RelationshipTransactionFactory.getInstance().getRelationship(relationshipEntityKey);
//
//            if (r != null) {
//                final String feedOwnersUUID = r.getForeignKey();
//                return UserServiceFactory.getInstance().getUserByKey(feedOwnersUUID);
//            }
//            throw new NimbitsException("Feed User not found");
//        }

    }

    private Point createFeedPoint(final User user) throws NimbitsException {

        final EntityName name = CommonFactoryLocator.getInstance().createName(Const.TEXT_DATA_FEED, EntityType.point);

        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.feed,
                ProtectionLevel.onlyConnection, user.getKey(), user.getKey(), UUID.randomUUID().toString());
        // final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

        Point point = PointModelFactory.createPointModel(entity);

        final Point result = (Point) EntityServiceFactory.getInstance().addUpdateEntity(point);


        postToFeed(user, "A new data point has been created for your data feed. Your data feed is just " +
                "a data point. Points are capable of storing numbers, text, json and xml data. Nimbits uses " +
                "a single data point to drive this feed.", FeedType.info);
        return result;

    }


}
