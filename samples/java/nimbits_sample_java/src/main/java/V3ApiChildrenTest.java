import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.io.Nimbits;

import java.util.UUID;

/**
 * Create a point and write the current time in ms to it until stopped
 *
 */
public class V3ApiChildrenTest {
    private static final String server = "http://localhost:8080";
    private static final String adminEmail = "admin@example.com";
    private static final String adminPassword = "password1234";


    public static void main(String... args) throws InterruptedException {

        Nimbits adminClient = new Nimbits.Builder()
                .email(adminEmail).token(adminPassword).instance(server).create();

        try {

            User admin = new UserModel.Builder().email(adminEmail).password(adminPassword).create();
            admin = adminClient.addUser(admin);

            Log("Created Admin: " + admin.toString());
        } catch (Throwable throwable) {
            //this will throw an exception if their already is an admin on this box
            Log(throwable.getMessage());
        }

        User me = adminClient.getMe();

        System.out.println(me.toString());



        for (int i = 0; i < 10; i++) {
            Point point = new PointModel.Builder().name("child_" + UUID.randomUUID()).create();
            point = adminClient.addPoint(me, point);

        }

        me = adminClient.getMe();
        Log("Got Children: " + me.getChildren().size());


    }

    private static void Log(String s) {
        System.out.println(s);
    }
}
