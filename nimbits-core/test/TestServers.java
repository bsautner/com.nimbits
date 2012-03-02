import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModel;
import com.nimbits.client.model.server.ServerModelFactory;
import com.nimbits.server.dao.server.ServerTransactionFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:13 PM
 */
public class TestServers {
    final String host="http://delete.me";
    Server createdServer;

    @Before
    public void createServer() throws NimbitsException {
        Server read = ServerTransactionFactory.getInstance().readServer(host);
        if (read != null) {
             ServerTransactionFactory.getInstance().deleteServer(read);
        }
        EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress("b@b.com");
        Server server = ServerModelFactory.createServer(host, emailAddress, Const.CONST_SERVER_VERSION);
        createdServer =  ServerTransactionFactory.getInstance().addUpdateServer(server);
        assertNotNull(createdServer);
        assertTrue(createdServer.getIdServer() > 0);
        assertNotNull(createdServer.getTs());
    }

    @After
    public void deleteServer() {
       //   ServerTransactionFactory.getInstance().deleteServer(createdServer);
    }

    @Test(expected = PersistenceException.class)
    public void testUniqueConstraint() throws NimbitsException {
      EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress("b@b.com");
      Server server = ServerModelFactory.createServer(host, emailAddress, Const.CONST_SERVER_VERSION);
      createdServer =  ServerTransactionFactory.getInstance().addUpdateServer(server);

    }


     @Test
    public void testServerDAORead() {


        Server read = ServerTransactionFactory.getInstance().readServer(host);
        assertNotNull(read);


    }

    @Test
    public void testServerHTTP() {
       EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress("htttp@b.com");
       Server server = ServerModelFactory.createServer("http://delete.me.http", emailAddress, "1.2.0");

        String json = GsonFactory.getInstance().toJson(server);
        String params = Const.Params.PARAM_JSON + "=" + json;
        String response = HttpCommonFactory.getInstance().doPost("http://localhost:8080/servers", params);
        System.out.println(response);
        Server retServer = GsonFactory.getInstance().fromJson(response, ServerModel.class);
        assertNotNull(retServer);
        assertTrue(retServer.getIdServer() > 0);
        assertNotNull(retServer.getTs());
        ServerTransactionFactory.getInstance().deleteServer(retServer);

    }


}
