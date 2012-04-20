package com.nimbits.server.transactions.service.point;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.transactions.service.entity.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 12:55 PM
 */
public class PointServiceTest extends NimbitsServletTest {
    private Point addPoint(EntityName name) throws NimbitsException {
        Entity e =  new EntityModel(name,
                "",
                EntityType.point,
                ProtectionLevel.everyone,
                user.getKey(),
                user.getKey(),
                UUID.randomUUID().toString());
        Point p =  PointModelFactory.createPointModel(e);
        return (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, p);
    }
    @Test
    public void addPointTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("FOO", EntityType.point);
        Point p =  addPoint(name);
        assertNotNull(p);
        List<Entity> x =  EntityTransactionFactory.getDaoInstance(user).getEntityByKey(p.getKey(), PointEntity.class);
        assertNotNull(x);
        assertFalse(x.isEmpty());

    }


}
