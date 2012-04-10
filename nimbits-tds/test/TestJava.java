import com.nimbits.server.orm.*;
import static org.junit.Assert.*;
import org.junit.*;

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
