import com.nimbits.server.http.HttpCommonFactory;
import org.junit.*;

import java.util.Random;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 11/21/11
 * Time: 1:59 PM
 */
public class HttpTest {

    String u = "http://api.wolframalpha.com/v2/query";
    String params = "appid=WL9JKJ-LYH57Y53TG";

    @Test
    @Ignore
    public void testTimeout() {
        Random random = new Random();


        params += "&input={";

        for (int i = 0; i < 10; i++) {
            params += random.nextDouble() + ",";
        }
        params = params.substring(0, params.length() - 1);

        params += "}";
        System.out.println(params);
        String r = HttpCommonFactory.getInstance().doPost(u, params);
        System.out.println(r);

    }


    @Test
    public void testWA() {

        params += "&input=calories 55 grams herring,roll";
        String r = HttpCommonFactory.getInstance().doPost(u, params);
        System.out.println(r);

    }


}
