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

import com.nimbits.client.model.point.*;
import static com.nimbits.client.model.point.PointModelFactory.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.point.*;
import org.junit.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/2/11
 * Time: 11:53 AM
 */
public class TestPointServiceImpl {

    @Test
    public void testPointProtectionOwnerRequestsConnectionsPublicPoint() {
        User loggedInUser = UserModelFactory.createUserModel(1001L);
        Point p = createPointModel(1L, 1002L);
        User pointOwner = UserModelFactory.createUserModel(1002);
        Assert.assertNotNull(p);
        p.setPublic(true);
        Assert.assertTrue("Accessing Stranger's Public Point", PointServiceFactory.getInstance().checkPointProtection(loggedInUser, pointOwner, p));

    }

    @Test
    public void testPointProtectionOwnerRequestsConnectionsPrivatePoint() {
        User loggedInUser = UserModelFactory.createUserModel(1001L);
        Point p = createPointModel(1L, 1002L);
        User pointOwner = UserModelFactory.createUserModel(1002);
        Assert.assertNotNull(p);
        p.setPublic(false);
        Assert.assertFalse("Accessing Stranger's Public Point", PointServiceFactory.getInstance().checkPointProtection(loggedInUser, pointOwner, p));

    }

}
