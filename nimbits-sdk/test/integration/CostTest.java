package integration;

import com.nimbits.client.NimbitsClient;
import com.nimbits.client.NimbitsClientFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.user.NimbitsUser;
import com.nimbits.user.UserFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 10:08 AM
 */
public class CostTest {

    @Test
    @Ignore
    public void testLoad() throws NimbitsException {

        String key = "33a53ddb-ced9-4832-862c-d6f238a929c0";
        String email = "bsautner@gmail.com";
        String url = "http://nimbits-qa.appspot.com";
        String pointName = UUID.randomUUID().toString();

        NimbitsUser u = UserFactory.createNimbitsUser(email, key);
        NimbitsClient client = NimbitsClientFactory.getInstance(u, url);
        Point point = client.addPoint(pointName);

        Random r = new Random();
        long start = new Date().getTime();

        for (int i = 0; i < 1000; i++) {
            client.recordValue(pointName, r.nextDouble());
            System.out.println(i);
        }
        long dur = (new Date().getTime() - start) / 1000;
        System.out.println("time: " + dur);
        client.deletePoint(pointName);


    }

}
