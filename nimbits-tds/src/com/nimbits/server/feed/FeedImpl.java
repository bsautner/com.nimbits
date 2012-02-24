package com.nimbits.server.feed;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.feed.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:02 PM
 */
public class FeedImpl extends RemoteServiceServlet implements Feed {


    @Override
    public void postToFeed(User user, Value value) {
        Map<String, Entity> map =  EntityServiceFactory.getInstance().getEntityMap(EntityType.feed);

       //Point point = PointServiceFactory.getInstance().getPointByUUID(user.getUuid());

        final Point point;
        if (map.size() == 0) {
           point = createFeedPoint(user);
        }
        else {
            Entity e =  map.values().iterator().next();
            point = PointServiceFactory.getInstance().getPointByUUID(e.getEntity());
        }
        if (point != null) {
            RecordedValueServiceFactory.getInstance().recordValue(user, point, value, false);
        }


    }

    @Override
    public Point createFeedPoint(User user) {
       String uuid = UUID.randomUUID().toString();
       Entity entity = EntityModelFactory.createEntity(user.getName(), "", EntityType.feed,
               ProtectionLevel.onlyMe, uuid, user.getUuid(), user.getUuid());
       Entity r = EntityServiceFactory.getInstance().addUpdateEntity(entity);
        return PointServiceFactory.getInstance().addPoint(user, r);


    }
}
