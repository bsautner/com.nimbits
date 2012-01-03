/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/27/11
 * Time: 1:27 PM
 */
public class PointTest {

    @Test
    public void createPoints() throws UnsupportedEncodingException {
        CategoryName cName = CommonFactoryLocator.getInstance().createCategoryName(UUID.randomUUID().toString());
        Category c = ClientHelper.client().addCategory(cName);

        for (int i = 0; i < 20; i++) {
            PointName pointName = CommonFactoryLocator.getInstance().createPointName(UUID.randomUUID().toString());
            Point p = ClientHelper.client().addPoint(c.getName(), pointName);
            assertNotNull(p);
            System.out.println(p.getName().toString());
        }
    }

    @Test
    public void testPointId() {
        ClientHelper.createSeedPoint();
    }

    @Test
    public void ensureDataPointPresent() throws Exception {
        CategoryName categoryName = CommonFactoryLocator.getInstance().createCategoryName(UUID.randomUUID().toString());
        PointName pointName = CommonFactoryLocator.getInstance().createPointName(UUID.randomUUID().toString());


        assertTrue(ClientHelper.client().isLoggedIn());
        ClientHelper.client().addCategory(categoryName);
        Point p = ClientHelper.client().addPoint(categoryName, pointName);
        assertNotNull(p);
    }


}
