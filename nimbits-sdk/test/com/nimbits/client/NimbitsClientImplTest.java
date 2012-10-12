package com.nimbits.client;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.user.NimbitsUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/14/12
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class NimbitsClientImplTest {

    private NimbitsClient client;
    private String p = "newPoint";
    private EntityName pointName;
    @Before
    public void setUp() throws Exception {
        String email =  "bsautner@nimbits.com";

        String password = "214631eb-8f4d-433a-8841-416c7789c87a";

        //  private final static String appscale =  "http://127.0.0.1:8888";
        String url ="http://nimbits1.appspot.com";
        EmailAddress em = CommonFactory.createEmailAddress(email);
        NimbitsUser g = new NimbitsUser(em, password);
        pointName = CommonFactory.createName(p, EntityType.point);
        client = NimbitsClientFactory.getInstance(g, url);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetHost() throws Exception {

    }

    @Test
    public void testGetAuthCookie() throws Exception {

    }

    @Test
    public void testIsLoggedIn() throws Exception {
        assertTrue(client.isLoggedIn());
    }

    @Test
    public void testGetUsers() throws Exception {

    }

    @Test
    public void testGetChart() throws Exception {

    }

    @Test
    public void testGetChartURL() throws Exception {

    }

    @Test
    public void testDeletePoint1() throws Exception {

    }

    @Test
    public void testDeletePoint2() throws Exception {

    }

    @Test
    public void testRecordValue1() throws Exception {

    }

    @Test
    public void testRecordValue2() throws Exception {

    }

    @Test
    public void testRecordValue3() throws Exception {

    }

    @Test
    public void testRecordValueWithGet() throws Exception {

    }

    @Test
    public void testRecordValue4() throws Exception {

    }

    @Test
    public void testRecordBatch() throws Exception {

    }

    @Test
    public void testRecordValue() throws Exception {

    }

    @Test
    public void testAddPoint1() throws Exception {
        Point px =  client.addPoint(p);
        assertNotNull(px);
        client.deletePoint(pointName);

    }

    @Test
    public void testAddCalculation() throws Exception {

    }

    @Test
    public void testAddPoint2() throws Exception {

    }

    @Test
    public void testGetCategories() throws Exception {

    }

    @Test
    public void testGetPoint() throws NimbitsException {
       Point px = client.getPoint("I DONT EXIST");
        assertNull(px);

    }

    @Test
    public void testUpdatePoint() throws Exception {

    }

    @Test
    public void testAddPoint() throws Exception {

    }

    @Test
    public void testCurrentValue() throws Exception {

    }

    @Test
    public void testGetCurrentDataObject() throws Exception {

    }

    @Test
    public void testRecordDataObject1() throws Exception {

    }

    @Test
    public void testRecordDataObject2() throws Exception {

    }

    @Test
    public void testGetCurrentRecordedValue() throws Exception {

    }

    @Test
    public void testGetSeries1() throws Exception {

    }

    @Test
    public void testGetSeries2() throws Exception {

    }

    @Test
    public void testGetSeries3() throws Exception {

    }

    @Test
    public void testDownloadSeries() throws Exception {

    }

    @Test
    public void testLoadSeriesFile() throws Exception {

    }

    @Test
    public void testGetChartImage() throws Exception {

    }

    @Test
    public void testGetNimbitsUser() throws Exception {

    }

    @Test
    public void testGetGoogleUser() throws Exception {

    }
}
