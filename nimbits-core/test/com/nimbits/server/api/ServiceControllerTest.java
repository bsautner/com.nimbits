package com.nimbits.server.api;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/21/12
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceControllerTest extends TestCase {
    public void testSearch() throws Exception {

    }

    public void testProcessRequest() throws Exception {

    }

    public void testReceiveLocation() throws Exception {

    }

    public void testGetEntities() throws Exception {

    }

    public void testMoveLocation() throws Exception {
       ServiceController s = new ServiceController();
       String r = s.moveLocation("(75.0000,-45.3333");
       assertNotNull(r);
    }

    public void testFixString() {
        ServiceController s = new ServiceController();
        String fixed = s.fixString("i lika do da ' chacha");
        assertNotNull(fixed);
    }
}
