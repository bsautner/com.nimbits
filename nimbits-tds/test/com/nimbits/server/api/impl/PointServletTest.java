package com.nimbits.server.api.impl;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.user.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 9:58 AM
 */
public class PointServletTest extends NimbitsServletTest {
    PointServletImpl i = new PointServletImpl();
    @Test
    public void createPointTest() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("test", EntityType.point);

         Point p = PointServletImpl.createPoint(user, name, null);
    }

    @Test
    public void testGet() {

        req.addParameter(Parameters.uuid.getText(), group.getKey());

        String r = i.processGet(req, resp);
        Category c = GsonFactory.getInstance().fromJson(r, CategoryModel.class);
        Assert.assertFalse(c.getChildren().isEmpty());



    }

    @Test
    public void getObjTest() throws NimbitsException {
        PointServletImpl.doInit(req, resp, ExportType.plain);
        String g = PointServletImpl.getPointObjects(group.getName().getValue(), null);
        List<Point> r = GsonFactory.getInstance().fromJson(g, GsonFactory.pointListType);
        assertFalse(r.isEmpty());
         Point px = r.get(0);
        assertNotNull(px.getFilterType());
        String p = PointServletImpl.getPointObjects(null, point.getName().getValue());
        Point pr = GsonFactory.getInstance().fromJson(p, PointModel.class);
        assertNotNull(pr);

    }

}
