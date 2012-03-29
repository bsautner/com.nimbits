package com.nimbits.server.entity;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import helper.*;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 2:27 PM
 */
public class EntityServiceTest extends NimbitsServletTest {


    @Test
    public void deleteEntityTest() throws NimbitsException {

        List<Entity> entities =  EntityServiceFactory.getInstance().getEntities();
        assertTrue(entities.size() > 0);
        Entity e = EntityServiceFactory.getInstance().getEntityByName(pointName);
        Entity c = EntityServiceFactory.getInstance().getEntityByName(pointChildName);
        Entity g = EntityServiceFactory.getInstance().getEntityByName(groupName);
        assertNotNull(e);
        assertNotNull(c);
        assertNotNull(g);
        List<Entity> children =  EntityServiceFactory.getInstance().getChildren(g, EntityType.point);
        assertTrue(children.size() > 0);

        EntityServiceFactory.getInstance().deleteEntity(g);
        e = EntityServiceFactory.getInstance().getEntityByName(pointName);
        c = EntityServiceFactory.getInstance().getEntityByName(pointChildName);
        g = EntityServiceFactory.getInstance().getEntityByName(groupName);


        assertNull(g);
        assertNull(e);
        assertNull(c);


    }

    @Test(expected=NimbitsException.class)
    public void duplicateNameTest() throws NimbitsException {

        EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.point);
        Entity model = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                UUID.randomUUID().toString(), user.getUuid(), user.getUuid());
        Entity e = EntityServiceFactory.getInstance().addUpdateEntity(model);
        Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                UUID.randomUUID().toString(), user.getUuid(), user.getUuid());
        Entity e2 = EntityServiceFactory.getInstance().addUpdateEntity(model2);


    }
    @Test
    public void duplicateNameGroupsOKTest()  {
        try {
            EntityName name = CommonFactoryLocator.getInstance().createName("TWICE", EntityType.category);
            Entity model = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    UUID.randomUUID().toString(), user.getUuid(), user.getUuid());
            Entity e = EntityServiceFactory.getInstance().addUpdateEntity(model);
            Entity model2 = EntityModelFactory.createEntity(name, "", EntityType.category, ProtectionLevel.everyone,
                    UUID.randomUUID().toString(), user.getUuid(), user.getUuid());

            Entity e2 = EntityServiceFactory.getInstance().addUpdateEntity(model2);
        } catch (NimbitsException e1) {
            fail();
        }


    }

    @Test
    public void getEntitiesTest() throws NimbitsException {

        List<Entity> entities =  EntityServiceFactory.getInstance().getEntities();
        assertTrue(entities.size() > 0);

    }

}
