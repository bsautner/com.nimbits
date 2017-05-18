package com.nimbits.it.console;

import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scenario - create several idle points that email me if they go idle, but write to them so they go back to
 * being not idle, then let them fall back to being idle so i get repeated email alerts
 */
@Ignore
public class IdleAlertSpamMe extends AbstractNimbitsTest {



    public static void main(String... args) throws Exception {

        IdleAlertSpamMe test = new IdleAlertSpamMe();
        test.setUp();
        test.runTest();


    }

    public void runTest() {
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Point point = nimbits.addPoint(user, new PointModel.Builder().idleAlarmOn(true).idleSeconds(60).create());

            System.out.println("Added Point " + point.getName());
            pointList.add(point);
            nimbits.addSubscription(point, new SubscriptionModel.Builder()
                    .subscriptionType(SubscriptionType.idle)
                    .subscribedEntity(point)
                    .enabled(true)
                    .notifyMethod(SubscriptionNotifyMethod.email)
                    .target("bsautner@gmail.com")
                    .create());
        }

        Random r = new Random();
        for (Point p : pointList) {
            nimbits.recordValueSync(p.getName().getValue(), new Value.Builder().doubleValue(r.nextDouble() * 100).create());
        }



        for (Point p : pointList) {
            System.out.println(nimbits.getSnapshot(p));
        }

    }
}
