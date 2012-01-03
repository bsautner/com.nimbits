import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import org.junit.Test;

import java.io.IOException;

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
        Point point = ClientHelper.createSeedPoint();
        assertNotNull(point);

        String params = "points=" + point.getName() +
                "&cht=lc&chs=200x200" +
                "&chxt=y&autoscale=true" +
                "&chco=000000,00FF00,0000FF,FF0000,FF0066,FFCC33,663333,003333" +
                "&chtt=" + "TEST" +
                "&chdl=" + point.getName();

        byte[] bytes = ClientHelper.client().getChartImage(ClientHelper.url, params);
        assertTrue(bytes.length > 0);


        ClientHelper.client().deletePoint(point.getName());

        point = ClientHelper.client().getPoint(point.getName());
        assertNull(point);

    }
}
