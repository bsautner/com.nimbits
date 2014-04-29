import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.helper.UserHelper;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class Program {
    private static EmailAddress email = CommonFactory.createEmailAddress("bsautner@gmail.com");
    private static String accessKey = "key";
    private static UrlContainer instanceUrl = UrlContainer.getInstance("cloud.nimbits.com");

    public static void main(String[] args) {
        System.out.println("Welcome To Nimbits!");

        //use an access key you created via the web console to get your session data
        Server server = ServerFactory.getInstance(instanceUrl);
        UserHelper sessionHelper = new UserHelper(server, email);
        List<BasicNameValuePair> additionalParams = new ArrayList<BasicNameValuePair>();
        additionalParams.add(new BasicNameValuePair(Parameters.key.name(), accessKey));
        User user = sessionHelper.getSession(additionalParams);
        System.out.println("Hello " + user.getEmail());








    }

}
