package com.nimbits.server.api.impl;

import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

import java.util.Random;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 1:48 PM
 */
public class BatchServletTest  extends NimbitsServletTest {


    @Test
    public void testGet() {
       BatchServletImpl i = new BatchServletImpl();
         Random r = new Random();
        double v1 = r.nextDouble();
        req.addParameter("p1", pointName.getValue());
        req.addParameter("v1", String.valueOf(v1));
        double v2 = r.nextDouble();
        req.addParameter("p2", pointChildName.getValue());
        req.addParameter("v2", String.valueOf(v2));
        i.doPost(req, resp);



    }


}
