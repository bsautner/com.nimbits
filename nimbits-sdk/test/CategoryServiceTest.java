/*
 * Copyright (c) 2011. Tonic Solutions, LLC. All Rights Reservered. This Code is distributed under GPL V3 without any warrenty.
 */

import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import org.junit.Assert;
import org.junit.Test;

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
    public void TestCategoryCrud() throws Exception {

        EntityName n = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString());
        assertTrue(ClientHelper.client().isLoggedIn());
        Category c = ClientHelper.client().addCategory(n);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.getId() > 0);
        Thread.sleep(1000);
        List<Category> l = ClientHelper.client().getCategories(true, true);
        Assert.assertNotNull(l);
        Thread.sleep(1000);
        boolean found = false;
        for (Category cx : l) {
            //  System.out.println(cx.getValue());
            if (cx.getName().equals(n)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);

        Thread.sleep(1000);
        Category cx2 = ClientHelper.client().getCategory(n, false, false);

        Assert.assertNotNull(cx2);

        Assert.assertEquals(cx2.getId(), c.getId());


        ClientHelper.client().deleteCategory(n);
        Thread.sleep(1000);
        l = ClientHelper.client().getCategories(true, true);
        found = false;
        for (Category cx : l) {

            if (cx.getName().equals(n)) {
                found = true;
                break;
            }
        }


        Assert.assertTrue(!found);

    }


}
