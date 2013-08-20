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
    private static final String base = "http://cloud.nimbits.com";
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
        System.out.println(v.getDoubleValue());

    }

}
