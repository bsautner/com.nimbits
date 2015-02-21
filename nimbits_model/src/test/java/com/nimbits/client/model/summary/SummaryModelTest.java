package com.nimbits.client.model.summary;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;


public class SummaryModelTest {
    @Test
    public void testIsReady() throws Exception {
        Entity e = EntityModelFactory.createEntity(CommonFactory.createName("test", EntityType.summary),
                "", EntityType.summary, ProtectionLevel.everyone, "me", "me");
        SummaryModel model = new SummaryModel();
        model.setLastProcessed(new Date());
        model.setSummaryIntervalMs(1000L);
        model.setSummaryType(SummaryType.average.getCode());
        assertFalse(model.isReady());
        //   Thread.sleep(1000);
        //    assertTrue(model.isReady());

    }
}
