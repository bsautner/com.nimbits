import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;

import java.util.UUID;

/**
 * Create a points with children
 *
 */
public class V3ApiChildrenTest extends NimbitsTest {

    public static void main(String... args) throws InterruptedException {

        V3ApiChildrenTest test = new V3ApiChildrenTest();
        test.execute();

    }
    @Override
    public void execute() throws InterruptedException {
        super.execute();

        User me = nimbits.getMe();

        System.out.println(me.toString());



        for (int i = 0; i < 10; i++) {
            Point point = new PointModel.Builder().name("child_" + UUID.randomUUID()).create();
            nimbits.addPoint(me, point);

        }

        me = nimbits.getMe();
        Log("Got Children: " + me.getChildren().size());


    }

    private static void Log(String s) {
        System.out.println(s);
    }
}
