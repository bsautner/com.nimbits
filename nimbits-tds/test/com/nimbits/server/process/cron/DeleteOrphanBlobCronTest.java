package com.nimbits.server.process.cron;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.file.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.valueblobstore.*;
import com.nimbits.server.*;
import com.nimbits.server.io.blob.*;
import com.nimbits.server.process.task.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.transactions.service.value.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.*;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/27/12
 * Time: 11:52 AM
 */
public class DeleteOrphanBlobCronTest extends NimbitsServletTest {


    private static final int D = 123;
    private static final double D1 = 1.23;

    @Test
    public void testCron() throws IOException, NimbitsException, InterruptedException {
        List<Value> values = new ArrayList<Value>(2);
        values.add(ValueModelFactory.createValueModel(D));
        values.add(ValueModelFactory.createValueModel(D1));

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
        Entity result =  EntityServiceFactory.getInstance().addUpdateEntity(f);

        List<ValueBlobStore> results = ValueTransactionFactory.getInstance(point).recordValues(values);
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
        DeleteOrphanedBlobTask.checkFile(first.getBlobKey(), true);


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
