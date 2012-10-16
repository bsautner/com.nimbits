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

package integration;

import com.nimbits.server.http.HttpCommonFactory;
import org.junit.*;

import java.util.Random;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 11/21/11
 * Time: 1:59 PM
 */
public class HttpTest {

    String u = "http://api.wolframalpha.com/v2/query";
    String params = "appid=WL9JKJ-LYH57Y53TG";

    @Test
    @Ignore
    public void testTimeout() {
        Random random = new Random();


        params += "&input={";

        for (int i = 0; i < 10; i++) {
            params += random.nextDouble() + ",";
        }
        params = params.substring(0, params.length() - 1);

        params += "}";
        System.out.println(params);
        String r = HttpCommonFactory.getInstance().doPost(u, params);
        System.out.println(r);

    }


    @Test
    public void testWA() {

        params += "&input=calories 55 grams herring,roll";
        String r = HttpCommonFactory.getInstance().doPost(u, params);
        System.out.println(r);

    }


}
