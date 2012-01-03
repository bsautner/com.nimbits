package com.nimbits.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.nimbits.server.http.HttpCommonFactory;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 11/21/11
 * Time: 11:16 AM
 */
public class TestHTTP  extends GWTTestCase {

    public void testTimeout() {
        String result = HttpCommonFactory.getInstance().doGet("http://google.com","");
       System.out.println(result);


    }


    @Override
    public String getModuleName() {
        return "com.nimbits.nimbits";
    }
}
