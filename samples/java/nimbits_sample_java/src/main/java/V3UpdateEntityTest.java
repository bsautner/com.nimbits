import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.server.gson.GsonFactory;

import java.util.UUID;


public class V3UpdateEntityTest extends NimbitsTest {


    public static void main(String... args) throws InterruptedException {

        V3UpdateEntityTest test = new V3UpdateEntityTest();
        test.execute();

    }
    @Override
    public void execute() throws InterruptedException {

        super.execute();
        String name = UUID.randomUUID().toString();
        Subscription subscription = new SubscriptionModel.Builder().name(name)
                .target("foo").create();
        Gson gson = GsonFactory.getInstance(true);
        String json = gson.toJson(subscription);
        log(json);
        Entity entity = gson.fromJson(json, Entity.class);
        Subscription subscription1 = (Subscription) entity;
        log(subscription1.getTarget());
        log("proved we can cast entities back and forth without knowing their class since we're so smart");

        nimbits.addSubscription(user, subscription);

        Optional<Subscription> optional = nimbits.findSubscription(name);
        if (optional.isPresent()) {
            Subscription r1 = optional.get();
            r1.setTarget("bar");
            nimbits.updateEntity(r1);
            Optional<Subscription> optional1 = nimbits.findSubscription(name);
            if (optional1.isPresent()) {
                Subscription r2 = optional1.get();
                if (! r2.getTarget().equals("bar")) {
                    throw new RuntimeException("subscription was returned but not updated");
                }
            } else {
                throw new RuntimeException("Error getting subscription");
            }
        }
        else {
            throw new RuntimeException("Error getting subscription");
        }




    }
}
