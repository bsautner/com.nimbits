/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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
