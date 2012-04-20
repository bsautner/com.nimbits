package com.nimbits.server.point;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.orm.PointEntity;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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
                null,

                null,
                null);
        return PointModelFactory.createPointModel(e);
    }
    @Test
    public void addPointTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("FOO", EntityType.point);
        Point p =  addPoint(name);
        assertNotNull(p);
        Point x = (Point) EntityTransactionFactory.getDaoInstance(user).getEntityByKey(p.getKey(), PointEntity.class).get(0);
        assertNotNull(x);
    }


}
