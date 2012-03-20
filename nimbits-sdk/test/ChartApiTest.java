import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/3/11
 * Time: 11:05 AM
 */
public class ChartApiTest {


    @Test
    public void testChartApi() throws InterruptedException, IOException, NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        Point point = ClientHelper.createSeedPoint(name);
        assertNotNull(point);
        Point result = ClientHelper.client().getPoint(name);
        assertNotNull(result);
        String params = "points=" + name +
                "&cht=lc&chs=200x200" +
                "&chxt=y&autoscale=true" +
                "&chco=000000,00FF00,0000FF,FF0000,FF0066,FFCC33,663333,003333" +
                "&chtt=" + "TEST" +
                "&chdl=" + name;

        byte[] bytes = ClientHelper.client().getChartImage(ClientHelper.url, params);
        assertTrue(bytes.length > 0);


        ClientHelper.client().deletePoint(name);

        point = ClientHelper.client().getPoint(name);
        assertNull(point);

    }
}
