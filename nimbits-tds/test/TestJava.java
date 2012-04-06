import com.nimbits.client.model.entity.*;
import com.nimbits.server.orm.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/6/12
 * Time: 11:34 AM
 */
public class TestJava {


    @Test
    public void test() throws ClassNotFoundException {

        String s = this.getClass().getName();
        assertEquals(Class.forName(s).getName(), s);
        System.out.println(PointEntity.class.getName());
    }


}
