import com.nimbits.client.enums.*;
import org.junit.*;
import static org.junit.Assert.assertNull;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 9:21 AM
 */
public class TestEnum {

    @Test
    public void testEnumFail() {
        DateFormatType type = DateFormatType.get("dddd");
        assertNull(type);
    }

}
