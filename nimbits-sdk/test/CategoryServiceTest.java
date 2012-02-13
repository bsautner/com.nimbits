/*
 * Copyright (c) 2011. Tonic Solutions, LLC. All Rights Reservered. This Code is distributed under GPL V3 without any warrenty.
 */


import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import org.junit.*;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/11
 * Time: 9:28 AM
 */
public class CategoryServiceTest {

    @Test
    @Ignore
    public void TestCategoryCrud() throws Exception {

        EntityName n = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString());
        assertTrue(ClientHelper.client().isLoggedIn());
        Entity c = ClientHelper.client().addCategory(n);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.getEntity().length() > 0);
        Thread.sleep(1000);
        List<Entity> l = ClientHelper.client().getCategories(true, true);
        Assert.assertNotNull(l);
        Thread.sleep(1000);
        boolean found = false;
        for (Entity cx : l) {
            //  System.out.println(cx.getValue());
            if (cx.getName().equals(n)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);

        Thread.sleep(1000);
        Entity cx2 = ClientHelper.client().getCategory(n, false, false);

        Assert.assertNotNull(cx2);

        Assert.assertEquals(cx2.getEntity(), c.getEntity());


        ClientHelper.client().deleteCategory(n);
        Thread.sleep(1000);
        l = ClientHelper.client().getCategories(true, true);
        found = false;
        for (Entity cx : l) {

            if (cx.getName().equals(n)) {
                found = true;
                break;
            }
        }


        Assert.assertTrue(!found);

    }


}
