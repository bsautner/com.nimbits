package com.nimbits.it.ha;

import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;

public class DuplicateNameTest extends AbstractBaseNimbitsTest {

    @Test(expected = retrofit.RetrofitError.class)
    public void testAddingDuplicatesFails() {

        String name = UUID.randomUUID().toString();

        Point point = adminClient.addPoint(adminUser, new PointModel.Builder().name(name).create());
        assertNotNull(point.getId());
        String id = point.getId();

        log("added a point", point);

         adminClient.addPoint(adminUser, new PointModel.Builder().name(name).create());




    }
}
