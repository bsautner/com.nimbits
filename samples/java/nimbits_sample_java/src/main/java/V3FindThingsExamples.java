import com.google.common.base.Optional;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;

import java.util.Random;
import java.util.UUID;

public class V3FindThingsExamples {




    public static void main(String[] args) throws InterruptedException {



        Test loadTester = new Test();
        loadTester.execute();


    }

    private static class Test extends NimbitsTest {


        public void execute() throws InterruptedException {
            super.execute();

            String pointName = UUID.randomUUID().toString();

            //create a point under the top level user with a random name
            Point point = new PointModel.Builder().name(pointName).parent(user.getKey()).highAlarmOn(true)
                    .highAlarm(100.00)
                    .lowAlarmOn(true)
                    .lowAlarm(0.0)
                    .create();
            Entity newPoint =  nimbits.addPoint(user, point);
            o("Created : " + newPoint.getName().getValue());

            Optional<Point> foundPoint = nimbits.findPointByName(pointName);
            if (foundPoint.isPresent()) {
                o("verified point");
                if (! foundPoint.get().isHighAlarmOn() || ! foundPoint.get().isLowAlarmOn()) {
                    throw new RuntimeException(" Alarm was off when it was set to on!");
                }
            }
            else {
                throw new RuntimeException("Point not found after being created and searched for");
            }

            //search for a point that was never created to test absent condition
            Optional<Point> shouldNotExist = nimbits.findPointByName(UUID.randomUUID().toString());
            if (shouldNotExist.isPresent()) {
                throw new RuntimeException("Point found that was never created!");
            }
            else {
                o("verified absent point");
            }


            //Record some values with the name only
            double testValue = 100.00 * new Random().nextDouble();
            Value value = new Value.Builder().doubleValue(testValue).create();
            nimbits.recordValue(pointName, value);

            Value retrieved = nimbits.getSnapshot(pointName);
            if (! retrieved.getDoubleValue().equals(value.getDoubleValue())) {
                throw new RuntimeException("Value was different!");
            }


            veryifyFindCategory();
            veryifyFindWebHook();

        }

        private void veryifyFindCategory() {
            String name = UUID.randomUUID().toString();

            Optional<Category> result = nimbits.findCategory("i don't exist");
            verifyReponse(result, false);

            Category category = new CategoryModel.Builder().name(name).create();
            nimbits.addCategory(user, category);

            Optional<Category> verify = nimbits.findCategory(name);
            verifyReponse(verify, true);
        }

        private void veryifyFindWebHook() {
            String name = UUID.randomUUID().toString();

            Optional<WebHook> result = nimbits.findWebHook("i don't exist");
            verifyReponse(result, false);

            WebHook webHook = new WebHookModel.Builder()
                    .name(name)
                    .setMethod(HttpMethod.GET)
                    .setUrl("http://www.foo.com")
                    .create();
            nimbits.addWebHook(user, webHook);

            Optional<WebHook> verify = nimbits.findWebHook(name);
            verifyReponse(verify, true);
        }

        private void verifyReponse(Optional result, boolean shouldExist) {
            if (! shouldExist && result.isPresent()) {
                throw new RuntimeException("Found an object that should not exist!");
            }
            else if (shouldExist && ! result.isPresent()) {
                throw new RuntimeException("Did NOT find an object that should exist!");
            }
            else {
                o("Got expected result looking for non existent entity");
            }
        }
    }


}
