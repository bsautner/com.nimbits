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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package helper;

import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.client.service.settings.*;
import com.nimbits.server.api.impl.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.point.*;
import com.nimbits.server.settings.*;
import com.nimbits.server.user.*;
import org.junit.*;
import org.springframework.mock.web.*;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 9:27 AM
 */
public class NimbitsServletTest {
    public static final String email = "test@example.com";
    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig(),
            new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("example.com");



    public MockHttpServletRequest req;
    public MockHttpServletResponse resp;


    public EntityName pointName;
    public EntityName pointChildName;
    public EntityName groupName;
    public User user;
    public EmailAddress emailAddress;

    public ValueServletImpl valueServlet;

    public PointService pointService;
    public SettingsService settingsService;
    public UserTransactions userTransactionsDao;
    public SettingTransactions settingsDAO;
    public Point point;
    public Point pointChild;
    public Entity pointEntity;
    public Entity pointChildEntity;



    @Before
    public void setUp() throws NimbitsException {

        helper.setUp();

        pointService = PointServiceFactory.getInstance();
        settingsService = SettingsServiceFactory.getInstance();
        emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
        valueServlet= new ValueServletImpl();
        req = new MockHttpServletRequest();
        pointName = CommonFactoryLocator.getInstance().createName("point", EntityType.point);
        pointChildName = CommonFactoryLocator.getInstance().createName("pointChild", EntityType.point);
        groupName = CommonFactoryLocator.getInstance().createName("group1", EntityType.point);
        resp = new MockHttpServletResponse();
        userTransactionsDao = UserTransactionFactory.getDAOInstance();
        User r = userTransactionsDao.createNimbitsUser(emailAddress);
        assertNotNull(r);
        user = userTransactionsDao.getNimbitsUser(emailAddress);
        assertNotNull(user);
        settingsDAO = SettingTransactionsFactory.getDaoInstance();

        req.addParameter(Parameters.email.getText(), email);
        req.addParameter(Parameters.secret.getText(),user.getSecret() );
        req.addParameter(Parameters.point.getText(), pointName.getValue());
        req.addParameter(Parameters.value.getText(), "1.234");

        Entity c = EntityModelFactory.createEntity(groupName, "", EntityType.category, ProtectionLevel.everyone, user.getKey(), user.getKey(), UUID.randomUUID().toString());
        c = EntityServiceFactory.getInstance().addUpdateEntity(c);

        pointEntity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone,  c.getKey(), user.getKey(), UUID.randomUUID().toString());
        point = (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, pointEntity);
       // point = pointService.addPoint(user, pointEntity);

        pointChildEntity = EntityModelFactory.createEntity(pointChildName, "", EntityType.point, ProtectionLevel.everyone, point.getKey(), user.getKey(), UUID.randomUUID().toString());
        pointChild = (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, pointChildEntity);
      // pointChild =  pointService.addPoint(user, pointChildEntity);
         assertNotNull(pointChild);

    }

    @After
    public void tearDown() {
        helper.tearDown();

    }
}
