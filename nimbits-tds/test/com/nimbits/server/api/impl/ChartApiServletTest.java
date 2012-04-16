package com.nimbits.server.api.impl;

import com.nimbits.server.*;
import org.junit.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 1:59 PM
 */
public class ChartApiServletTest extends NimbitsServletTest {

     @Test
     public void testProcessGet() {

         ChartApiServletImpl.processGet(req, resp);
         System.out.println(resp.getContentLength());

     }

}
