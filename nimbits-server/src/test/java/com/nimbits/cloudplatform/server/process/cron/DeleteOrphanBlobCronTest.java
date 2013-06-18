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

package com.nimbits.cloudplatform.server.process.cron;


import com.nimbits.cloudplatform.server.NimbitsServletTest;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class DeleteOrphanBlobCronTest extends NimbitsServletTest {


    private static final int D = 123;
    private static final double D1 = 1.23;

//    @Test
//    @Ignore
//    public void testCron() throws IOException, Exception, InterruptedException {
//        List<Value> values = new ArrayList<Value>(2);
//        values.add(ValueFactory.createValueModel(D));
//        values.add(ValueFactory.createValueModel(D1));
//
//        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
//        assertFalse(iterator.hasNext());
//
//        EntityName name = CommonFactory.createName("gg", EntityType.file);
//        String key = BlobStoreFactory.getInstance().createFile(name, "some text", ExportType.plain);
//
//        EntityName nameLost = CommonFactory.createName("lost name", EntityType.file);
//        String keyLost = BlobStoreFactory.getInstance().createFile(name, "some lost text", ExportType.plain);
//
//        assertNotNull(key);
//        Entity e = EntityModelFactory.createEntity(name, "", EntityType.file, ProtectionLevel.everyone,
//                user.getKey(), user.getKey());
//        com.nimbits.cloudplatform.client.model.file.File f = FileFactory.createFile(e, key);
//        Entity result =  EntityServiceImpl.addUpdateSingleEntity(f);
//
//        List<ValueBlobStore> results = valueDao.recordValues(point, values);
//        assertFalse(results.isEmpty());
//        assertEquals(results.size(), 1);
//        assertNotNull(result);
//
//        Iterator<BlobInfo> iterator2 = new BlobInfoFactory().queryBlobInfos();
//        int count = 0;
//        assertTrue(iterator2.hasNext());
//        while (iterator2.hasNext()) {
//            BlobInfo k = iterator2.next();
//            assertNotNull(k);
//                      count ++;
//
//        }
//        assertEquals(3, count);
//
//        Iterator<BlobInfo> iteratorD = new BlobInfoFactory().queryBlobInfos();
//        BlobInfo first = iteratorD.next();
//     //   DeleteBlobTask.checkFile(first.getBlobKey(), true);
//
//
//        Iterator<BlobInfo> iterator3 = new BlobInfoFactory().queryBlobInfos();
//        int count2 = 0;
//        assertTrue(iterator3.hasNext());
//        while (iterator3.hasNext()) {
//            BlobInfo k = iterator3.next();
//            assertNotNull(k);
//            count2 ++;
//
//        }
//        assertEquals(2, count2);
//    }

}
