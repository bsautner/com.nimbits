package com.nimbits.client.model.schedule;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ScheduleModelTest {

    @Test
    public void TestCreate() {
        EntityName name = CommonFactory.createName("name", EntityType.schedule);

        Entity e = EntityModelFactory.createEntity(name, "", EntityType.schedule, ProtectionLevel.onlyMe
                , "b@b.com", "b@b.com");
        assertNotNull(e);
    }
}
