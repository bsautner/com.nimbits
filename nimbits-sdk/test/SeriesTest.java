/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/28/11
 * Time: 12:58 PM
 */
public class SeriesTest {


    @Test
    @Ignore
    public void testGetLargeSeries() throws Exception {
        Random rx = new Random();
        Point p = new PointModel();

       EntityName name = (CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString()));
        EntityName categoryName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString());
       // Category c = ClientHelper.client().addCategory(categoryName);
        Point rp = ClientHelper.client().addPoint(name, p);
       // assertNotNull(c);
        assertNotNull(rp);

        assertTrue(ClientHelper.client().isLoggedIn());
        for (int i = 0; i < 1009; i++) {

            ClientHelper.client().recordValue(name, rx.nextDouble() * 1000, new Date(new Date().getTime() - (5000 - i)));

        }

        Calendar s = Calendar.getInstance();
        s.set(2009, 0, 1);

        List<Value> r = ClientHelper.client().getSeries(name, s.getTime(), new Date());
        assertTrue(r.size() > 0);
        assertTrue(r.size() > 1000);
        ClientHelper.client().deletePoint(name);
    }

    @Test
    @Ignore
    public void testFileDownload() {
        Calendar s = Calendar.getInstance();
        String fn = "/tmp/b1.json";
        s.set(2009, 0, 1);
        Point p = new PointModel();

       EntityName name = (CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString()));
       // EntityName categoryName = CommonFactoryLocator.getInstance().createName(Const.CONST_HIDDEN_CATEGORY);
        ClientHelper.client().addPoint(name.getValue());
        try {
            Random rx = new Random();

            for (int i = 0; i < 100; i++) {

                ClientHelper.client().recordValue(name, rx.nextDouble() * 1000, new Date(new Date().getTime() - (5000 - i)));

            }

            ClientHelper.client().downloadSeries(name, s.getTime(), new Date(), fn);

            File f = new File(fn);

            assertTrue(f.exists());
            if (f.exists()) {
                List<Value> r = ClientHelper.client().loadSeriesFile(fn);
                assertTrue(r.size() > 1);

                f.delete();

            }
            ClientHelper.client().deletePoint(name);
        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
