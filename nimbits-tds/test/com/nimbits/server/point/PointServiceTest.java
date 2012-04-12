package com.nimbits.server.point;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 12:55 PM
 */
public class PointServiceTest extends NimbitsServletTest {

    @Test
    public void addPointTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("FOO", EntityType.point);
        Point p = PointServiceFactory.getInstance().addPoint(name);
        assertNotNull(p);
        Point x = (Point) EntityTransactionFactory.getDaoInstance(null).getEntityByKey(p.getKey(), PointEntity.class).get(0);
        assertNotNull(x);
    }


}
