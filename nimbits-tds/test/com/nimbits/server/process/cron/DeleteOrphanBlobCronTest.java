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

package com.nimbits.server.process.cron;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.file.FileFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.io.blob.BlobStoreFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml"
})
public class DeleteOrphanBlobCronTest extends NimbitsServletTest {


    private static final int D = 123;
    private static final double D1 = 1.23;

    @Test
    @Ignore
    public void testCron() throws IOException, NimbitsException, InterruptedException {
        List<Value> values = new ArrayList<Value>(2);
        values.add(ValueFactory.createValueModel(D));
        values.add(ValueFactory.createValueModel(D1));

        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
        assertFalse(iterator.hasNext());

        EntityName name = CommonFactoryLocator.getInstance().createName("gg", EntityType.file);
        String key = BlobStoreFactory.getInstance().createFile(name, "some text", ExportType.plain);

        EntityName nameLost = CommonFactoryLocator.getInstance().createName("lost name", EntityType.file);
        String keyLost = BlobStoreFactory.getInstance().createFile(name, "some lost text", ExportType.plain);

        assertNotNull(key);
        Entity e = EntityModelFactory.createEntity(name, "", EntityType.file, ProtectionLevel.everyone,
                user.getKey(), user.getKey());
        com.nimbits.client.model.file.File f = FileFactory.createFile(e, key);
        Entity result =  entityService.addUpdateEntity(f);

        List<ValueBlobStore> results = valueDao.recordValues(point, values);
        assertFalse(results.isEmpty());
        assertEquals(results.size(), 1);
        assertNotNull(result);

        Iterator<BlobInfo> iterator2 = new BlobInfoFactory().queryBlobInfos();
        int count = 0;
        assertTrue(iterator2.hasNext());
        while (iterator2.hasNext()) {
            BlobInfo k = iterator2.next();
            assertNotNull(k);
                      count ++;

        }
        assertEquals(3, count);

        Iterator<BlobInfo> iteratorD = new BlobInfoFactory().queryBlobInfos();
        BlobInfo first = iteratorD.next();
     //   DeleteBlobTask.checkFile(first.getBlobKey(), true);


        Iterator<BlobInfo> iterator3 = new BlobInfoFactory().queryBlobInfos();
        int count2 = 0;
        assertTrue(iterator3.hasNext());
        while (iterator3.hasNext()) {
            BlobInfo k = iterator3.next();
            assertNotNull(k);
            count2 ++;

        }
        assertEquals(2, count2);
    }

}
