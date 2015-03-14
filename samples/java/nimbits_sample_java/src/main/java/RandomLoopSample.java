import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.AccessCode;
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

    private static final AccessCode ACCESS_KEY = AccessCode.getInstance("key");


    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("192.168.1.15:8080");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL, EMAIL_ADDRESS, ACCESS_KEY);


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome To Nimbits!");


        String pointName = "foo";



        //use an access key you created via the web console to get your user data

        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER);

        User user = sessionHelper.getSession();


        System.out.println("Hello " + user.getEmail());

        //record some data!

        Random r = new Random();
        ValueHelper valueHelper = HelperFactory.getValueHelper(SERVER);
        while (true) {

            try {
                valueHelper.recordValue(pointName, r.nextDouble() * 100);
                System.out.println("Recorded Value: "  );

            } catch (Exception e) {
               //let's just keep on trucking for this sample.
            }
            Thread.sleep(5000);

        }



    }

}
