import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
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
 * your user name and created a new global read/write key withthe value of "key"
 *
 * When you run this program it will download your user session, create a data point with a random name, feed it some
 * random values, then download the series and delete the point.
 *
 */
public class SimpleJavaSample {
    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("bsautner@gmail.com");
    private static final String ACCESS_KEY = "key";

    //set this to your appid.appspot.com if on google app engine, cloud.nimbits.com for the public cloud,
    //and localhost:8080 if that's your local instance.
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("cloud.nimbits.com");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL);


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome To Nimbits!");

        //some random name - can be anything but duplicates are not allowed.
        String pointName = UUID.randomUUID().toString();



        //use an access key you created via the web console to get your user data

        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);

        User user = sessionHelper.getSession();


        System.out.println("Hello " + user.getEmail());


        //Create A Data Point

        PointHelper pointHelper = HelperFactory.getPointHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);

        pointHelper.createPoint(pointName, "Some Random Description");

        boolean success = pointHelper.pointExists(pointName);

        if (success) {
            System.out.println("Created Point " + pointName);
            //record some data!

            Random r = new Random();
            ValueHelper valueHelper = HelperFactory.getValueHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
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
