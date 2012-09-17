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
import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.file.FileFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.io.blob.BlobStoreFactory;
import com.nimbits.server.process.task.DeleteBlobTask;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml"
})
public class BlobStoreTest extends NimbitsServletTest {

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testNull() {
        BlobKey key = new BlobKey(null);


    }

    @Test
    public void deleteOrphansTest() throws NimbitsException, IOException, InterruptedException {

        EntityName name = CommonFactoryLocator.getInstance().createName("gg", EntityType.file);
        String key = BlobStoreFactory.getInstance().createFile(name, "some text", ExportType.plain);

        EntityName nameLost = CommonFactoryLocator.getInstance().createName("lost name", EntityType.file);
        String keyLost = BlobStoreFactory.getInstance().createFile(name, "some lost text", ExportType.plain);

        assertNotNull(key);
        Entity e = EntityModelFactory.createEntity(name, "", EntityType.file, ProtectionLevel.everyone,
                user.getKey(), user.getKey());
        com.nimbits.client.model.file.File f = FileFactory.createFile(e, key);
        Entity result =  entityService.addUpdateEntity(f);
        assertNotNull(result);

     //   int r = DeleteOrphanBlobCron.processRequest();
     //   assertEquals(2, r);




        Iterator<BlobInfo> iterator =  new BlobInfoFactory().queryBlobInfos();
        int count = 0;
        while (iterator.hasNext()){
            final BlobInfo i = iterator.next();
           req.removeAllParameters();
            req.addParameter(Parameters.key.getText(), i.getBlobKey().getKeyString());
          DeleteBlobTask.processRequest(req);
        }
      //  int r2 = DeleteOrphanBlobCron.processRequest();
      //  assertEquals(1, r2);

//       // BlobKey r = BlobStoreFactory.getInstance().deleteOrphans(null);
//
//        List<Entity> result2 =  EntityTransactionFactory.getInstance(user).getEntityByBlobKey(new BlobKey(key));
//        assertFalse(result2.isEmpty());
//
//        List<Entity> resultLost =  EntityTransactionFactory.getInstance(user).getEntityByBlobKey(new BlobKey(keyLost));
//        assertTrue(resultLost.isEmpty());
    }


}
