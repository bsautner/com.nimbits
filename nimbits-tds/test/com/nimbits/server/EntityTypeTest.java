package com.nimbits.server;

import com.nimbits.client.enums.EntityType;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 3:18 PM
 */
public class EntityTypeTest {

    @Test
    public void testClasses() {

        for (EntityType type : EntityType.values()) {


           Class cls = null;
            try {
                cls = Class.forName(type.getClassName());
            } catch (Exception e) {
              fail();
            }
            assertNotNull(cls);

        }

    }


}
