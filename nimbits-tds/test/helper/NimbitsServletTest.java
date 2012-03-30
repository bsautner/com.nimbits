package helper;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.server.api.impl.ValueServletImpl;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.settings.SettingsServiceFactory;
import com.nimbits.server.user.UserTransactionFactory;
import com.nimbits.server.user.UserTransactions;
import org.junit.After;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

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

    public Point point;



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
        point = pointService.addPoint(user, e);

        Entity e2 = EntityModelFactory.createEntity(pointChildName, "", EntityType.point, ProtectionLevel.everyone, UUID.randomUUID().toString(),e.getEntity(), user.getUuid());
        pointService.addPoint(user, e2);


    }

    @After
    public void tearDown() {
        helper.tearDown();

    }
}
