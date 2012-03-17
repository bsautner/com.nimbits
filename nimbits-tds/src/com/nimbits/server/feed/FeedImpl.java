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
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.user.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:02 PM
 */
public class FeedImpl extends RemoteServiceServlet implements Feed {
    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }
    }

    @Override
    public void postToFeed(final User user, Entity entity, Point originalPoint, final Value value) throws NimbitsException {
        final Point point = getFeedPoint(user);
        if (point != null) {
            FeedValue feedValue = new FeedValueModel(valueToHtml(user,entity, originalPoint, value), value.getData());
            String json = GsonFactory.getSimpleInstance().toJson(feedValue);
            Value v = ValueModelFactory.createValueModel(value, json);
            RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);
        }
    }

    public void postToFeed(final User user, final String html) throws NimbitsException {
        final Point point = getFeedPoint(user);
        StringBuilder sb = new StringBuilder() ;
        sb.append("<p><img src=\"" + ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest()) +
                "/resources/images/logo.png\" align=\"left\" width=\"40\" height=\"40\">");
        sb.append(html);
        sb.append("</p>");
        FeedValue feedValue = new FeedValueModel(sb.toString(), "");
        String json = GsonFactory.getSimpleInstance().toJson(feedValue);
        Value value = ValueModelFactory.createValueModel(0.0, 0.0, Const.CONST_IGNORED_NUMBER_VALUE,
                new Date(), point.getUUID(), "", json);
        Value v = ValueModelFactory.createValueModel(value, json);
        RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);

    }

    private String valueToHtml(User user, Entity entity, Point point, Value value) {
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

    private Point getFeedPoint(User user) throws NimbitsException {
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
    public List<FeedValue> getFeed(int count) throws NimbitsException {
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

        EntityName name = CommonFactoryLocator.getInstance().createName("Subscription Data Feed", EntityType.point);

        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.feed,
                ProtectionLevel.onlyMe, uuid, user.getUuid(), user.getUuid());
        final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

        Point point =  PointServiceFactory.getInstance().addPoint(user, r);

        postToFeed(user, "A new data point has been created for your data feed. Your data feed is just " +
                "a data point. Points are capable of storing numbers, text, json and xml data. Nimbits uses " +
                "a single data point to drive this feed.");
        return point;

    }
}
