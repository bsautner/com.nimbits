/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package integration;/*
 * Copyright (c) 2011. Nimbits Inc. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
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
        Point p = PointModelFactory.createPointModel(null);

       EntityName name = (CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point));
        EntityName categoryName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
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
    public void testFileDownload() throws NimbitsException {
        Calendar s = Calendar.getInstance();
        String fn = "/tmp/b1.json";
        s.set(2009, 0, 1);
        Point p = PointModelFactory.createPointModel(null);

       EntityName name = (CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point));
       // EntityName categoryName = CommonFactory.createName(Const.CONST_HIDDEN_CATEGORY);
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
