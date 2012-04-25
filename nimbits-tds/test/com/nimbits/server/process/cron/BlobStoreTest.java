package com.nimbits.server.process.cron;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.file.*;
import com.nimbits.server.*;
import com.nimbits.server.io.blob.*;
import com.nimbits.server.process.task.*;
import com.nimbits.server.transactions.service.entity.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 12:24 PM
 */
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
        Entity result =  EntityServiceFactory.getInstance().addUpdateEntity(f);
        assertNotNull(result);

        int r = DeleteOrphanBlobCron.processRequest();
        assertEquals(2, r);




        Iterator<BlobInfo> iterator =  new BlobInfoFactory().queryBlobInfos();
        int count = 0;
        while (iterator.hasNext()){
            final BlobInfo i = iterator.next();
           req.removeAllParameters();
            req.addParameter(Parameters.key.getText(), i.getBlobKey().getKeyString());
          DeleteOrphanedBlobTask.processRequest(req);
        }
        int r2 = DeleteOrphanBlobCron.processRequest();
        assertEquals(1, r2);

//       // BlobKey r = BlobStoreFactory.getInstance().deleteOrphans(null);
//
//        List<Entity> result2 =  EntityTransactionFactory.getInstance(user).getEntityByBlobKey(new BlobKey(key));
//        assertFalse(result2.isEmpty());
//
//        List<Entity> resultLost =  EntityTransactionFactory.getInstance(user).getEntityByBlobKey(new BlobKey(keyLost));
//        assertTrue(resultLost.isEmpty());
    }


}
