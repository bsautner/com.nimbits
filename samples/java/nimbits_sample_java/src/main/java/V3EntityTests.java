import com.nimbits.client.enums.EntityType;

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
    }
}
