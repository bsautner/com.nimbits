/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.user.User;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 6/24/11
 * Time: 2:44 PM
 */
public class UserTest {

    @Test
    @Ignore
    public void testGetAllUsersQA() throws NimbitsException {
        List<User> users = ClientHelper.client().getUsers();
        assertTrue(ClientHelper.client().isLoggedIn());
        Assert.assertNotNull(users);
        Assert.assertTrue(users.size() > 1);
        System.out.println(users.size());


    }


    @Test
    @Ignore
    public void testGetAllUsersProd() throws NimbitsException {
        List<User> users = ClientHelper.client().getUsers();
        Assert.assertNotNull(users);
        Assert.assertTrue(users.size() > 1);

    }

//    @Test
//    @Ignore
//    public void countUsersTest() throws IOException {
//        List<User> users = ClientHelper.specificVersion().getUsers();
//
//        Assert.assertNotNull(users);
//        Assert.assertTrue(users.size() > 1);
//        System.out.println(users.size());
//        System.out.println(ClientHelper.meOnProd().isLoggedIn());
//        PointName pointName = (PointName) CommonFactoryLocator.getInstance().createPointName("usercount");
//        ClientHelper.meOnProd().deletePoint(pointName);
//        CategoryName cat = CommonFactoryLocator.getInstance().createCategoryName(Const.CONST_HIDDEN_CATEGORY);
//
//        ClientHelper.meOnProd().addPoint(cat, pointName);
//        double i = 0.0;
//        for (User u : users) {
//            i++;
//            System.out.println(u.getEmail() + " " + u.getDateCreated());
//            // double prev = Double.valueOf(ClientHelper.meOnProd().currentValue("usercount"));
//
//            //   ClientHelper.meOnProd().recordValue(pointName, i, u.getDateCreated());
//
//
//        }
//
//    }


}
