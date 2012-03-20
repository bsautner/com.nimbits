package com.nimbits.server.feed;

import com.google.gson.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
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
import com.nimbits.server.point.*;
import com.nimbits.server.value.*;
import com.nimbits.server.user.*;
import org.apache.commons.lang3.exception.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:02 PM
 */
public class FeedImpl extends RemoteServiceServlet implements Feed {
    private static final Logger log = Logger.getLogger(FeedImpl.class.getName());

    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }

    @Override
    public void postToFeed(final User user, final Entity entity, final Point originalPoint, final Value value) throws NimbitsException {
        final Point point = getFeedPoint(user);
        if (point != null) {
            final FeedValue feedValue = new FeedValueModel(shortenFeedHTML(valueToHtml(user, entity, originalPoint, value)), value.getData());
            final String json = GsonFactory.getSimpleInstance().toJson(feedValue);
            final Value v = ValueModelFactory.createValueModel(value, json);
            RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);
        }
    }

    @Override
    public void postToFeed(final User user, final NimbitsException ex) {
        final Point point;
        try {
            point = getFeedPoint(user);
            if (point != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("<p><img src=\"" + ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()) +
                        "/resources/images/symbol-error.png\" align=\"left\" width=\"35\" height=\"35\">");
                sb.append("<p style=\"color:red\">Error reported<p>");
                sb.append("<p style=\"color:red\">" + ex.getMessage() + "<p>");
                sb.append("<p>" + ExceptionUtils.getStackTrace(ex) + "<p>");
                final FeedValue feedValue = new FeedValueModel(shortenFeedHTML(sb.toString()), "");
                final String json = GsonFactory.getSimpleInstance().toJson(feedValue);
                final Value value = ValueModelFactory.createValueModel(0.0, 0.0, Const.CONST_IGNORED_NUMBER_VALUE,
                        new Date(), point.getUUID(), "", json);
                final Value v = ValueModelFactory.createValueModel(value, json);
                RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);
            }
        } catch (NimbitsException e) {
             log.severe(e.getMessage());
        }

    }

    public void postToFeed(final User user, final String html) throws NimbitsException {
        final Point point = getFeedPoint(user);
        final StringBuilder sb = new StringBuilder() ;
        sb.append("<p><img src=\"" + ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()) +
                "/resources/images/logo.png\" align=\"left\" width=\"40\" height=\"40\">");
        sb.append(html);
        sb.append("</p>");
        final FeedValue feedValue = new FeedValueModel(shortenFeedHTML(sb.toString()), "");
        final String json = GsonFactory.getSimpleInstance().toJson(feedValue);
        final Value value = ValueModelFactory.createValueModel(0.0, 0.0, Const.CONST_IGNORED_NUMBER_VALUE,
                new Date(), point.getUUID(), "", json);
        final Value v = ValueModelFactory.createValueModel(value, json);
        RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);

    }

    private String shortenFeedHTML(final String html) {
        if (html.length() > Const.DEFAULT_FEED_LENGTH) {
            try {
                String shorterHtml;
                shorterHtml =  (html.length() > 2000) ?  html.substring(0, 2000) : html;
                return html.substring(0, Const.DEFAULT_FEED_LENGTH)
                        + "<a href=\"#\" onclick=\"window.open('feed.html?content=" + URLEncoder.encode(shorterHtml, Const.CONST_ENCODING) + "', 'Feed'," +
                        "'height=400,width=400,toolbar=0,status=0,location=0' );\" >" +
                        "&nbsp;[more]</a>";
//                        + "<a href=\"feed.html?content=" + URLEncoder.encode(html, Const.CONST_ENCODING) + "\" " +
//                        "target=\"_blank\">" +
//                        "&nbsp;[more]</a>";
            } catch (UnsupportedEncodingException e) {
                return html.substring(0, Const.DEFAULT_FEED_LENGTH);
            }

        }
        else {
            return html;
        }

    }

    private String valueToHtml(final User user, final Entity entity, final Point point, final Value value) {
        StringBuilder sb = new StringBuilder();
        if (! (Double.compare(value.getNumberValue(), Const.CONST_IGNORED_NUMBER_VALUE) == 0)) {
            sb.append("<img align=\"left\" src=\"")
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
            sb.append("&nbsp;").append("<a href=\"")
                    .append(ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()))
                    .append("?uuid=")
                    .append(point.getUUID())
                    .append("\">")
                    .append(entity.getName().getValue())
                    .append("</a>")
                    .append("<br>");
        }


        if (! (Double.compare(value.getNumberValue(), Const.CONST_IGNORED_NUMBER_VALUE) == 0)) {
            sb.append("Alert&nbsp;Status:")
                    .append(value.getAlertState().name());
            sb.append("&nbsp;&nbsp;Value:")
                    .append(value.getNumberValue());
        }


        if (! Utils.isEmptyString(value.getNote())) {
            sb.append(("<br>Note:" + value.getNote()));
        }



        return sb.toString();
    }

    private Point getFeedPoint(final User user) throws NimbitsException {
        final Point point;
        final Map<String, Entity> map =  EntityServiceFactory.getInstance().getEntityMap(user, EntityType.feed);

        if (map.size() == 0) {
            point = createFeedPoint(user);
        }
        else {
            Entity e =  map.values().iterator().next();
            point = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());
        }
        return point;
    }

    @Override
    public List<FeedValue> getFeed(final int count) throws NimbitsException {
        User user = getUser();
        final Point point = getFeedPoint(user);
        List<Value> values = RecordedValueServiceFactory.getInstance().getTopDataSeries(point, count, new Date());
        List<FeedValue> retObj = new ArrayList<FeedValue>();


        for (Value v : values) {
            if (! Utils.isEmptyString(v.getData())) {
                try {
                    FeedValue fv =  GsonFactory.getInstance().fromJson(v.getData(), FeedValueModel.class);
                    retObj.add(fv);
                } catch (JsonSyntaxException ignored) {

                }
            }
        }
        return retObj;
    }

    private Point createFeedPoint(final User user) throws NimbitsException {
        final String uuid = UUID.randomUUID().toString();

        final EntityName name = CommonFactoryLocator.getInstance().createName("Subscription Data Feed", EntityType.point);

        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.feed,
                ProtectionLevel.onlyMe, uuid, user.getUuid(), user.getUuid());
        final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

        final Point point =  PointServiceFactory.getInstance().addPoint(user, r);

        postToFeed(user, "A new data point has been created for your data feed. Your data feed is just " +
                "a data point. Points are capable of storing numbers, text, json and xml data. Nimbits uses " +
                "a single data point to drive this feed.");
        return point;

    }
}
