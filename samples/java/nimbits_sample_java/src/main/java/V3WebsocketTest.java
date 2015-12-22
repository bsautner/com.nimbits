
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.instance.InstanceModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.io.socket.SocketConnection;
import com.nimbits.client.io.socket.SocketListener;

import java.io.IOException;

/**
 * An example of writing values to a data point and also having a web socket open to recieve those events.
 * EMAIL_ADDRESS : the account owners email
 * ACCESS_KEY : an access key you created by logging into the web console at INSTANCE_URL
 * INSTANCE_URL : The url to the nimbits server instance
 */

public class V3WebsocketTest extends NimbitsTest {



    public static void main(String[] args) {

        V3WebsocketTest v3WebsocketTest = new V3WebsocketTest();
        v3WebsocketTest.execute();

    }

    @Override
    public void execute() {


        AccessKey accessKey = new AccessKeyModel.Builder().code(PASSWORD).create();

        Instance instance = new InstanceModel.Builder()
                .baseUrl(UrlContainer.getInstance(INSTANCE_URL))
                .adminEmail(CommonFactory.createEmailAddress(EMAIL_ADDRESS))
                .apiKey(accessKey).create();

        try {
            SocketConnection socketConnection = new SocketConnection(instance, new SocketListener() {


                public void onOpen(Connection connection) {
                    System.out.println("connected!");
                }

                public void onClose(int closeCode, String message) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}