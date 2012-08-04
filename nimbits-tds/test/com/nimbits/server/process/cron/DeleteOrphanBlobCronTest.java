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
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.value.ValueTransactionFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
