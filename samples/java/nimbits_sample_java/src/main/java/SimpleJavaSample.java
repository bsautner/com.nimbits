import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.AccessCode;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.PointHelper;
import com.nimbits.io.helper.UserHelper;
import com.nimbits.io.helper.ValueHelper;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * This program assumes you have logged into the target nimbits SERVER on the INSTANCE_URL used below, right clicked on
 * your user name and created a new global read/write key with the value of "key"
 *
 * When you run this program it will download your user session, create a data point with a random name, feed it some
 * random values, then download the series and delete the point.
 *
 */
public class SimpleJavaSample {
    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("support@nimbits.com");

    //this sample uses an access key, so you've logged into nimbits and right clicked on your account to create this read/write key with user scope.

    private static final AccessCode ACCESS_KEY = AccessCode.getInstance("key");

    //set this to your appid.appspot.com if on google app engine, cloud.nimbits.com for the public cloud,
    //and localhost:8080 if that's your jetty local instance for example.
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL, EMAIL_ADDRESS, ACCESS_KEY);


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome To Nimbits!");

        //some random name - can be anything but duplicates are not allowed.
        String pointName = UUID.randomUUID().toString();



        //use an access key you created via the web console to get your user data

        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER);

        User user = sessionHelper.getSession();


        System.out.println("Hello " + user.getEmail());


        //Create A Data Point

        PointHelper pointHelper = HelperFactory.getPointHelper(SERVER);

        pointHelper.createPoint(pointName, "Some Random Description");

        boolean success = pointHelper.pointExists(pointName);

        if (success) {
            System.out.println("Created Point " + pointName);
            //record some data!

            Random r = new Random();
            ValueHelper valueHelper = HelperFactory.getValueHelper(SERVER);
            for (int i =0; i < 10; i++) {

                Value value = valueHelper.recordValue(pointName, r.nextDouble() * 100);
                System.out.println("Recorded Value: " + value.toString());
                Thread.sleep(10);


            }

            List<Value> recordedValues = valueHelper.getSeries(pointName);
            System.out.println("downloaded " + recordedValues.size() + " Values.");

        }
        else {
            System.out.println("Something Went Wrong! Maybe the key or email was invalid, or the server isn't running.");

        }


















    }

}
