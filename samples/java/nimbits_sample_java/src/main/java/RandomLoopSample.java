import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.UserHelper;
import com.nimbits.io.helper.ValueHelper;

import java.util.Random;

/**
 * This program assumes you have logged into the target nimbits SERVER on the INSTANCE_URL used below, right clicked on
 * your user name and created a new global read/write key with the value of "key"
 *
 * When you run this program it write random values to a pre-existing data point named foo
 *
 */
public class RandomLoopSample {
    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("support@nimbits.com");

    //this sample uses an access key, so you've logged into nimbits and right clicked on your account to create this read/write key with user scope.

    private static final String ACCESS_KEY = "key";

    //set this to your appid.appspot.com if on google app engine, cloud.nimbits.com for the public cloud,
    //and localhost:8080/nimbits if that's your jetty local instance for example.
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("192.168.1.15:8080/nimbits");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL);


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome To Nimbits!");


        String pointName = "foo";



        //use an access key you created via the web console to get your user data

        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);

        User user = sessionHelper.getSession();


        System.out.println("Hello " + user.getEmail());

        //record some data!

        Random r = new Random();
        ValueHelper valueHelper = HelperFactory.getValueHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        while (true) {

            try {
                Value value = valueHelper.recordValue(pointName, r.nextDouble() * 100);
                System.out.println("Recorded Value: " + value.getValueWithData());

            } catch (Exception e) {
               //let's just keep on trucking for this sample.
            }
            Thread.sleep(5000);

        }



    }

}
