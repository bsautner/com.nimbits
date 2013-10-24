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

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;
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

})
public class BlobStoreTest extends NimbitsServletTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        BlobKey key = new BlobKey(null);


    }


//    @Test
//    public void deleteOrphansTest() , IOException, InterruptedException {
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
//        com.nimbits.client.model.file.File f = FileFactory.createFile(e, key);
//        Entity result =  EntityServiceImpl.addUpdateSingleEntity(f);
//        assertNotNull(result);
//
//     //   int r = DeleteOrphanBlobCron.processRequest();
//     //   assertEquals(2, r);
//
//
//
//
//        Iterator<BlobInfo> iterator =  new BlobInfoFactory().queryBlobInfos();
//        int count = 0;
//        while (iterator.hasNext()){
//            final BlobInfo i = iterator.next();
//           req.removeAllParameters();
//            req.addParameter(Parameters.key.getText(), i.getBlobKey().getKeyString());
//          DeleteBlobTask.processRequest(req);
//        }
//      //  int r2 = DeleteOrphanBlobCron.processRequest();
      //  assertEquals(1, r2);

//       // BlobKey r = BlobStoreFactory.getInstance().deleteOrphans(null);
//
//        List<Entity> result2 =  EntityTransactionFactory.getInstance(user).getEntityByBlobKey(new BlobKey(key));
//        assertFalse(result2.isEmpty());
//
//        List<Entity> resultLost =  EntityTransactionFactory.getInstance(user).getEntityByBlobKey(new BlobKey(keyLost));
//        assertTrue(resultLost.isEmpty());
//    }


}
