import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityDescription;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModelFactory;
import com.nimbits.server.dao.pointDescription.EntityJPATransactionFactory;
import com.nimbits.server.dao.server.ServerTransactionFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner  hello
 * User: BSautner
 * Date: 12/14/11
 * Time: 5:08 PM
 */
public class TestPointDesc {
    String host = "http://delete.me";
    Server createdServer;
    PointName pointName = CommonFactoryLocator.getInstance().createPointName("testPointFromJunit");

    @Before
    public void createServer() throws NimbitsException {
        host = UUID.randomUUID().toString();

        Server read = ServerTransactionFactory.getInstance().readServer(UUID.randomUUID().toString());
        if (read != null) {
            ServerTransactionFactory.getInstance().deleteServer(read);
        }
        EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress("b@b.com");
        Server server = ServerModelFactory.createServer(host, emailAddress, "1.2.0");
        createdServer = ServerTransactionFactory.getInstance().addUpdateServer(server);
        assertNotNull(createdServer);
        assertTrue(createdServer.getIdServer() > 0);
        assertNotNull(createdServer.getTs());
    }

    @After
    public void deleteServer() {
        System.out.println("after");
        ServerTransactionFactory.getInstance().deleteServer(createdServer);
    }

    @Test
    public void testUpdatePoint() {

        String pointUUID = UUID.randomUUID().toString();

        String d1 = "desc 1";
        String d2 = "desc 2";

        EntityDescription p = EntityModelFactory.createEntityDescription(createdServer, pointName, pointUUID, d1, EntityType.point);
        EntityDescription ret = EntityJPATransactionFactory.getInstance().addUpdateEntityDescription(p);

        assertNotNull(ret);
        assertEquals(ret.getDesc(), d1);

        EntityDescription p2 = EntityModelFactory.createEntityDescription(createdServer, pointName, pointUUID, d2, EntityType.point);
        EntityDescription ret2 = EntityJPATransactionFactory.getInstance().addUpdateEntityDescription(p2);

        assertNotNull(ret);
        assertEquals(ret2.getDesc(), d2);

        EntityDescription p3 = EntityJPATransactionFactory.getInstance().getEntityDescriptionByUUID(pointUUID);
        assertEquals(d2, p3.getDesc());


    }

    @Test
    public void testDeletePoint() {

        String pointUUID = UUID.randomUUID().toString();

        String d1 = "desc 1";
        String d2 = "desc 2";

        EntityDescription p = EntityModelFactory.createEntityDescription(createdServer, pointName, pointUUID, d1, EntityType.point);
        EntityDescription ret = EntityJPATransactionFactory.getInstance().addUpdateEntityDescription(p);

        assertNotNull(ret);
        assertEquals(ret.getDesc(), d1);

        EntityJPATransactionFactory.getInstance().deleteEntityDescriptionByUUID(pointUUID);

        EntityDescription p3 = EntityJPATransactionFactory.getInstance().getEntityDescriptionByUUID(pointUUID);
        assertNull(p3);


    }


    @Test
    public void testSearch() throws NimbitsException {


        List<EntityDescription> response = EntityJPATransactionFactory.getInstance().searchEntityDescription("test");
        assertNotNull(response);
        assertTrue(response.size() > 0);
        System.out.println(response.size());
    }


    @Test
    public void testPointDesc() {


        EntityDescription p = EntityModelFactory.createEntityDescription(createdServer, pointName, UUID.randomUUID().toString(), "test", EntityType.point);
        EntityDescription ret = EntityJPATransactionFactory.getInstance().addEntityDescription(p);

        assertNotNull(ret);
        assertTrue(ret.getIdPoint() > 0);

    }

    @Test
    public void testReadPointDesc() {


        EntityDescription p = EntityModelFactory.createEntityDescription(createdServer, pointName, UUID.randomUUID().toString(), "test", EntityType.point);
        EntityDescription ret = EntityJPATransactionFactory.getInstance().addEntityDescription(p);

        assertNotNull(ret);
        assertTrue(ret.getIdPoint() > 0);


    }

    @Test
    public void testHTTP() {
        Point point = PointModelFactory.createPointModel(0, 1);
        point.setUuid(UUID.randomUUID().toString());
        point.setName(CommonFactoryLocator.getInstance().createPointName(UUID.randomUUID().toString()));
        point.setDescription("A Description");

        String json = GsonFactory.getInstance().toJson(point);
        String params = Const.PARAM_JSON + "=" + json;
        params += "&" + Const.PARAM_ACTION + "=" + Action.update;
        params += "&" + Const.PARAM_HOST + "=" + host;

        String response = HttpCommonFactory.getInstance().doPost("http://localhost:8080/pointdescriptions", params);
        System.out.println(response);
        assertNotNull(response);
//           Server retServer = GsonFactory.getInstance().fromJson(response, ServerModel.class);
//           assertNotNull(retServer);
//           assertTrue(retServer.getIdServer() > 0);
//           assertNotNull(retServer.getTs());
        //ServerTransactionFactory.getInstance().deleteServer(retServer);

    }


}
