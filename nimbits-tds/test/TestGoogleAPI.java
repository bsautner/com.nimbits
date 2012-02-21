import com.nimbits.server.google.*;
import static junit.framework.Assert.*;
import org.junit.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/6/12
 * Time: 2:53 PM
 */
public class TestGoogleAPI {

    @Test
    public void testURLShr() throws IOException {

        String retVal = "";
        retVal = GoogleURLShortener.shortenURL("http://www.nimbits.com");
        assertNotNull(retVal);
        assertTrue(retVal.contains("http"));


    }

}
