/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform;


import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.helper.ValueHelper;

import java.util.Random;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 11:31 AM
 */
public class Program {
    private static final String base = "http://653.nimbits-02.appspot.com";
    private static final String email = "bsautner@gmail.com";
    private final static String key = "key";
    // private final static String point = "P2";

    public static void main(String[] args) {
        Nimbits.setLoginListener(new Nimbits.LoginListener() {
            @Override
            public void loginSuccess(User session) {
                System.out.println("Logged in With Key");




                //     Random r = new Random();
                while (true) {

//                    Value v = null;
//                    try {
//                        v = ValueHelper.recordValue(point, r.nextDouble());
//                    } catch (Exception e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//                    System.out.println(v.getDoubleValue());
                    try {
                        testSummary();
                        Thread.sleep(1000);
                    } catch (RuntimeException e) {

                    } catch (InterruptedException e) {

                    }

                }
            }

            @Override
            public void loginFail(String reason) {
                System.out.println(reason);
            }
        });
        Nimbits.loginWithKey(base, email, key);

    }

    public static void testSummary() {
        String p1 = "summary_test";
        Value v = ValueHelper.recordValue(p1, 1.0);
        System.out.println("Returned: " + v.getDoubleValue());

    }

}
