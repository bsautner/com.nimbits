package com.nimbits.server.admin.system;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.user.UserServiceFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;

import java.util.List;
import java.util.UUID;

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
            Point pm = PointModelFactory.createPointModel(ep, 0.0, EXPIRE, "", 0.0, false, false, false, 0, false, FilterType.none, 0.0, false);
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
