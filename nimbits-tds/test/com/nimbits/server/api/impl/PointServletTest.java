package com.nimbits.server.api.impl;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import helper.*;
import org.junit.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 9:58 AM
 */
public class PointServletTest extends NimbitsServletTest {

    @Test
    public void createPointTest() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("test", EntityType.point);

         Point p = PointServletImpl.createPoint(user, name, null);
    }
}
