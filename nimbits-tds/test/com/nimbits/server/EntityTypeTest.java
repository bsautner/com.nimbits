package com.nimbits.server;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.*;

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
