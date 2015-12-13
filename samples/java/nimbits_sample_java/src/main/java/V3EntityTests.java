import com.nimbits.client.enums.EntityType;

/**
 * Create each type of entity and verify it does what it should
 */
public class V3EntityTests extends NimbitsTest {


    public static void main(String... args) throws InterruptedException {

        V3EntityTests test = new V3EntityTests();
        test.execute();



    }

    @Override
    public void execute() throws InterruptedException {
        super.execute();
        for (EntityType type : EntityType.values()) {

            log(type.name());

        }


//                user
//                point
//                category
//                subscription
//                sync
//                calculation
//                summary
//                accessKey
//                instance
//                socket
//                connection
//                schedule
//                webhook

        log("Done " +  getClass().getName());

    }
}
