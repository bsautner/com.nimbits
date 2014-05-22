
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.ApiKey;
import com.nimbits.client.model.server.apikey.ApiKeyFactory;
import com.nimbits.io.socket.SocketConnection;
import com.nimbits.io.socket.SocketListener;

/**
 * An example of writing values to a data point and also having a web socket open to recieve those events.
 * EMAIL_ADDRESS : the account owners email
 * ACCESS_KEY : an access key you created by logging into the web console at INSTANCE_URL
 * INSTANCE_URL : The url to the nimbits server instance
 */

public class SocketSample {


    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("support@nimbits.com");

    //a running jetty server with nimbits installed (using nimbits.war)
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("192.168.1.14:8080/nimbits");

    //you can create this server object with an API KEY you configured your server with to make authentication easy

    private static final ApiKey API_KEY = ApiKeyFactory.createApiKey("KEY");
    private static final Server SERVER = ServerFactory.getInstance(INSTANCE_URL, API_KEY);


    public static void main(String[] args) throws Exception {

        //See the Nimbits Model Project for this enum.  You can tell nimbits what kind of socket to open.
        //live will send all new values to the client
        //basic will only send new values with a subscription set to sockets.




        SocketConnection socketConnection = new SocketConnection(SERVER, EMAIL_ADDRESS, new SocketListener() {


            public void onOpen(Connection connection)
            {
                System.out.println("connected!");
            }

            public void onClose(int closeCode, String message)
            {
                System.out.println("closing socket: " + message);
            }

            @Override
            public void onNotify(Point point) {
                System.out.println("A subscription to a point's events has been processed");
                System.out.println(point.getName().getValue());
                System.out.println(point.getValue().getAlertState().name());
               // System.out.println(point.getValue().getData());
            }

            @Override
            public void onUpdate(Point point) {
                System.out.println("A point was updated");
                System.out.println(point.getName().getValue());
                System.out.println(point.getValue().getAlertState().name());
               // System.out.println(point.getValue().getData());
            }
        });

        socketConnection.sendMessage("Hello Nimbits Socket! " + System.currentTimeMillis());
    }
}