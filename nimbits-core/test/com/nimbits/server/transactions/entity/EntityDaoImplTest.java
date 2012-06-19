package com.nimbits.server.transactions.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.server.orm.JpaEntity;
import com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: benjamin
 * Date: 5/26/12
 * Time: 9:36 AM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-daos.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class EntityDaoImplTest {

    private EntityJPATransactions entityTransactions;

    @Resource(name="entityDao")
    public void setInstanceTransactions(EntityJPATransactions transactions) {
        this.entityTransactions = transactions;
    }


    @Test
    public void testSearch2() {
        List<Entity> e = entityTransactions.searchEntity("description");
        assertTrue(e.size() > 0);
    }

    @Test
    public void testSearchEntity() throws Exception {
        EntityName name = CommonFactoryLocator.getInstance().createName("name", EntityType.point);
        Entity e = EntityModelFactory.createEntity(name, "description",
                EntityType.point, ProtectionLevel.onlyMe, "b@b.com", "b@b.com", UUID.randomUUID().toString());

        entityTransactions.addEntity(e, "http://localhost");

        EntityName name2 = CommonFactoryLocator.getInstance().createName("name 2", EntityType.point);
        Entity e2 = EntityModelFactory.createEntity(name2, "description 2",
                EntityType.point, ProtectionLevel.onlyMe, "b@b.com", "b@b.com", UUID.randomUUID().toString());



        Entity r =  entityTransactions.addEntity(e2, "http://localhost");


    }

    @Test
    public void testAddEntity() throws Exception {
        EntityName name = CommonFactoryLocator.getInstance().createName("name", EntityType.point);
        Entity e = EntityModelFactory.createEntity(name, "description",
                EntityType.point, ProtectionLevel.onlyMe, "b@b.com", "b@b.com", UUID.randomUUID().toString());
        assertNotNull(e);


    }

    @Test
    public void testAddUpdateEntity() throws Exception {

        EntityName name = CommonFactoryLocator.getInstance().createName("name", EntityType.point);
        Entity e = EntityModelFactory.createEntity(name, "description",
                EntityType.point, ProtectionLevel.onlyMe, "b@b.com", "b@b.com", UUID.randomUUID().toString());
        assertNotNull(e);
        entityTransactions.addEntity(e, "http://localhost");
        e.setDescription("updated");
        Entity x =  entityTransactions.addUpdateEntity(e, "http://localhost");
        assertEquals("updated", x.getDescription());

    }

    @Test
    public void testAddUpdateEntityGPS() throws Exception {

        String uuid = UUID.randomUUID().toString();
        EntityName name = CommonFactoryLocator.getInstance().createName("name", EntityType.point);
        Entity e = EntityModelFactory.createEntity(name, "description",
                EntityType.point, ProtectionLevel.onlyMe, "b@b.com", "b@b.com",uuid );
        assertNotNull(e);
        entityTransactions.addEntity(e, "http://localhost");
        e.setDescription("updated");
        Entity x =  entityTransactions.addUpdateEntity(e, "http://localhost");
        assertEquals("updated", x.getDescription());

        // entityTransactions.getEntityByUUID()
    }

    @Test
    public void updateGPSTest() throws NimbitsException {
        Entity x = entityTransactions.getEntityByUUID("3a202d8f-21e1-4070-8725-786026187da3");

        entityTransactions.updateLocation(x, "40.01999,-75.147171");




    }

    @Test
    public void getAllEntities() {
        List<JpaEntity> list = entityTransactions.getAllEntities();
        assertNotNull(list);


    }
    @Test
    public void getEntitiesGPS() {
        List<JpaEntity> list = entityTransactions.getAllEntities();
        assertNotNull(list);
        for (JpaEntity entity : list) {
            String x = entityTransactions.getLocation(entity);
            System.out.println(x);
        }


    }
    @Test

    public void testGetEntityByUUID() throws Exception {
        String uuid = UUID.randomUUID().toString();
        EntityName name = CommonFactoryLocator.getInstance().createName("name", EntityType.point);
        Entity e = EntityModelFactory.createEntity(name, "description",
                EntityType.point, ProtectionLevel.onlyMe, "b@b.com", "b@b.com", uuid);
        assertNotNull(e);
        entityTransactions.addEntity(e, "http://localhost");


        Entity found = entityTransactions.getEntityByUUID(uuid);
        assertNotNull(found);
        assertEquals("description", found.getDescription());

    }





    @Test( )
    public void testDeleteEntityByUUID() throws Exception {
        String uuid = UUID.randomUUID().toString();
        EntityName name = CommonFactoryLocator.getInstance().createName("name", EntityType.point);
        Entity e = EntityModelFactory.createEntity(name, "description",
                EntityType.point, ProtectionLevel.onlyMe, "b@b.com", "b@b.com", uuid);
        assertNotNull(e);
        entityTransactions.addEntity(e, "http://localhost");

        entityTransactions.deleteEntityByUUID(uuid);

//        Entity found = entityTransactions.getEntityByUUID(uuid);
//        assertNull(found);

    }
}
