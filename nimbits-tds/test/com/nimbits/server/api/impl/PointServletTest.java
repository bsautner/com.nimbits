package com.nimbits.server.api.impl;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
