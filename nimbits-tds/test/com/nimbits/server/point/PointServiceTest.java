package com.nimbits.server.point;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.orm.*;
import static org.junit.Assert.*;
import org.junit.*;

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
