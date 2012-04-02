import com.nimbits.client.exception.*;
import com.nimbits.server.dao.search.*;
import com.nimbits.server.jpa.JpaSearchLog;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.junit.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:23 PM
 */
public class TestSearchLog {

    @Test
    public void testSearchLogging() throws NimbitsException {
        final String search = "test";
        SearchLogTransactionFactory.getInstance().addUpdateSearchLog(search);
        JpaSearchLog j = SearchLogTransactionFactory.getInstance().readSearchLog(search);
        assertNotNull(j);
        SearchLogTransactionFactory.getInstance().addUpdateSearchLog(search);
        JpaSearchLog j2 = SearchLogTransactionFactory.getInstance().readSearchLog(search);
        assertNotNull(j2);
        assertTrue(j.getSearchCount()+1 == j2.getSearchCount());

    }
}
