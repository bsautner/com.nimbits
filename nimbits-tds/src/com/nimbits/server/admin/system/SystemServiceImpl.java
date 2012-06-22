package com.nimbits.server.admin.system;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.transactions.service.user.*;
import com.nimbits.server.transactions.service.value.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/27/12
 * Time: 4:32 PM
 */
public class SystemServiceImpl implements SystemService{


    private static final int EXPIRE = 365;

    @Override
    public void updateSystemPoint(String pointName, double value, boolean incrementAsCounter) throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(pointName, EntityType.point);
        User admin =UserServiceFactory.getServerInstance().getAdmin();
        List<Entity> e = EntityServiceFactory.getInstance().getEntityByName(admin,
                name, EntityType.point);
        Point p;
        if (e.isEmpty()) {
            String ownerKey = admin.getKey();
            Entity ep = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.onlyMe, ownerKey,
                    ownerKey, UUID.randomUUID().toString());
            Point pm = PointModelFactory.createPointModel(ep, 0.0, EXPIRE, "", 0.0, false, false, false, 0, false, FilterType.none, 0.0);
            p = (Point) EntityServiceFactory.getInstance().addUpdateEntity(admin, pm);
        }
        else {
            p = (Point) e.get(0);

        }
        Value vx;
        if (incrementAsCounter) {
           List<Value> c = ValueServiceFactory.getInstance().getCurrentValue(p);
           double cd = ! c.isEmpty() ? c.get(0).getDoubleValue() : 0.0;
           vx = ValueFactory.createValueModel(cd + value);
        }
        else {
            vx = ValueFactory.createValueModel(value);
        }
        ValueServiceFactory.getInstance().recordValue(admin, p, vx);

    }
}
