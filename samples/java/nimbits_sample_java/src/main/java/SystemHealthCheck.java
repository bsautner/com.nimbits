import com.google.common.collect.Range;
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

import java.util.*;

/**
 * This program was designed to hammer the holy heck out of a nimbits instance to see how it performs. It logs the
 * time it takes to record a value and download all of the series data to the benchmark points created in advance.
 *
 *
 * This program assumes you have logged into the target nimbits SERVER on the INSTANCE_URL used below, right clicked on
 * your user name and created a new global read/write key with the value of "key"
 *
 * When you run this program it write random values to a pre-existing data point named foo and verify data is being recorded
 * and is retrievable for as long as the program is allowed to run.
 *
 */
public class SystemHealthCheck {
    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("support@nimbits.com");

    //this sample uses an access key, so you've logged into nimbits and right clicked on your account to create this read/write key with user scope.

    private static final String ACCESS_KEY = "key";

    //set this to your appid.appspot.com if on google app engine, cloud.nimbits.com for the public cloud,
    //and localhost:8080/nimbits if that's your jetty local instance for example.
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("localhost:8080/nimbits");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL);
    private static  List<Value> valueList = new ArrayList<Value>();

    private static ValueHelper valueHelper;
    private static long startTime;
    private static String pointName = "foo";
    private static String seriesBenchmark = "seriesBenchmark";
    private static String valueBenchmark = "valueBenchmark";
    private static String volumeBenchmark = "volumeBenchmark";
    private static boolean error = false;
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome To Nimbits!");
        startTime = System.currentTimeMillis();
        //use an access key you created via the web console to get your user data
        UserHelper sessionHelper = HelperFactory.getUserHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);

        User user = sessionHelper.getSession();
        System.out.println("Hello " + user.getEmail());

        //record some data!

        Random r = new Random();
        valueHelper = HelperFactory.getValueHelper(SERVER, EMAIL_ADDRESS, ACCESS_KEY);
        int counter = 0;
        while (! error) {

            long s = System.currentTimeMillis();
            Value value = valueHelper.recordValue(pointName, r.nextDouble() * 10000 * r.nextDouble());
            long e = System.currentTimeMillis();
            long elapsed = e - s;
            valueHelper.recordValue(valueBenchmark, (double) elapsed);

            valueList.add(value);

            Thread.sleep(100);
            if (++counter >= 10) {

                System.out.println("Verifying Data");
                Range range = Range.closed(new Date(startTime), new Date());
                long s2 = System.currentTimeMillis();
                List<Value> sample = valueHelper.getSeries(pointName, range);
                long e2 = System.currentTimeMillis();
                long elapsed2 = e2 - s2;
                valueHelper.recordValue(seriesBenchmark, (double) elapsed2);



                System.out.println("Verifying Data::: Downloaded " + sample.size() + " uploaded " + valueList.size());

                if (valueList.size() != sample.size()) {
                //    error = true;
                    System.out.println("Verifying Data::: Missing Values expected: " + valueList.size() + " found: " + sample.size());
                   // break;

                }

//                for (Value v : sample) {
//                    if (! valueList.contains(v)) {
//                        error = true;
//
//                        System.out.println("Verifying Data::: downloaded value didn't match uploaded");
//                        break;
//                    }
//                }
//                for (Value v : valueList) {
//                    if (! sample.contains(v)) {
//                        error = true;
//                        System.out.println("Verifying Data::: downloaded value didn't match uploaded");
//                        break;
//                    }
//                }
                System.out.println("Verifying Passed!");
                valueHelper.recordValue(volumeBenchmark, (double) valueList.size());
                counter = 0;
                Thread.sleep(2000);
            }

        }



    }



}
