package helper;

import com.google.appengine.tools.development.*;
import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
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

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 9:27 AM
 */
public class NimbitsServletTest {
    public final String email = "test@example.com";
    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("example.com");
         //   new LocalTaskQueueTestConfig());


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
        userTransactionsDao.createNimbitsUser(emailAddress);
        user = userTransactionsDao.getNimbitsUser(emailAddress);
        req.addParameter(Parameters.email.getText(), email);
        req.addParameter(Parameters.secret.getText(),user.getSecret() );
        req.addParameter(Parameters.point.getText(), pointName.getValue());
        req.addParameter(Parameters.value.getText(), "1.234");

        Entity c = EntityModelFactory.createEntity(groupName, "", EntityType.category, ProtectionLevel.everyone, UUID.randomUUID().toString(), user.getUuid(), user.getUuid());
        c = EntityServiceFactory.getInstance().addUpdateEntity(c);

        Entity e = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone, UUID.randomUUID().toString(), c.getEntity(), user.getUuid());
        pointService.addPoint(user, e);

        Entity e2 = EntityModelFactory.createEntity(pointChildName, "", EntityType.point, ProtectionLevel.everyone, UUID.randomUUID().toString(),e.getEntity(), user.getUuid());
        pointService.addPoint(user, e2);


    }

    @After
    public void tearDown() {
        helper.tearDown();

    }
}
