package com.nimbits.cloudplatform;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProgramTest {
    //  private static final String base = "http://localhost:8080";
    private static final String base = "http://cloud.nimbits.com";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMain() throws Exception {

    }




    @Test
    public void keyTest() throws InterruptedException {
//        String p = "Pressure";
//        String locEmail = "drproductsadm@gmail.com";
//        String key = "secret";
//        final boolean[] done = {false};
//        final boolean[] loggedIn = new boolean[1];
//        Nimbits.instance.setLoginListener(new Nimbits.LoginListener() {
//            @Override
//            public void loginSuccess(User session) {
//                System.out.print("login with key ok");
//                done[0] = true;
//                loggedIn[0] = true;
//            }
//
//            @Override
//            public void loginFail(String reason) {
//                System.out.print(reason);
//                done[0] = true;
//                loggedIn[0] = false;
//
//            }
//        });
//
//        Nimbits.instance.loginWithKey(base, locEmail, key);
//        while (!done[0]) {
//            Thread.sleep(10);
//        }
//        assertTrue(loggedIn[0]);
//
//        List<Entity> e = Transaction.getEntity(SimpleValue.getInstance(locEmail + "/Pressure"), EntityType.point, PointModel.class);
//        assertFalse(e.isEmpty());
//        System.out.print(e.get(0).getUUID());

        //Transaction.postValue(e.get(0), ValueFactory.createValueModel(0.1725));
//
//        List<Value> vx = Transaction.getSeries(e.get(0), Range.between(0, 100));
//        assertFalse(vx.isEmpty());
//        for (Value v : vx) {
//            System.out.println(v.getDoubleValue());
//        }
    }

    @Test
    public void keyTest2() throws InterruptedException {
//        String p = "Pressure";
//        String locEmail = "drproductsadm@gmail.com";
//        String key = "secret";
//        final boolean[] done = {false};
//        final boolean[] loggedIn = new boolean[1];
//        Nimbits.instance.setLoginListener(new Nimbits.LoginListener() {
//            @Override
//            public void loginSuccess(User session) {
//                System.out.print("login with key ok");
//                done[0] = true;
//                loggedIn[0] = true;
//            }
//
//            @Override
//            public void loginFail(String reason) {
//                System.out.print(reason);
//                done[0] = true;
//                loggedIn[0] = false;
//
//            }
//        });
//
//        Nimbits.instance.loginWithKey(base, locEmail, key);
//        while (!done[0]) {
//            Thread.sleep(10);
//        }
//        assertTrue(loggedIn[0]);
//
//        List<Entity> e = Transaction.getEntity(SimpleValue.getInstance(locEmail + "/Pressure"), EntityType.point, PointModel.class);
//        assertFalse(e.isEmpty());
//        System.out.print(e.get(0).getUUID());
//
//
//
//        List<Value> vx = Transaction.getSeries(e.get(0), Range.between(0, 100));
//        assertFalse(vx.isEmpty());
//        for (Value v : vx) {
//            System.out.println(v.getDoubleValue());
//        }
    }

    @Test
    public void compressionTest() throws InterruptedException {
//        String p = "compression_test";
//        String p2 = "one_to_one_calc";
//        String locEmail = "bsautner@gmail.com";
//        String key = "key";
//        final boolean[] done = {false};
//        final boolean[] loggedIn = new boolean[1];
//        Nimbits.instance.setLoginListener(new Nimbits.LoginListener() {
//            @Override
//            public void loginSuccess(User session) {
//                System.out.print("login with key ok");
//                done[0] = true;
//                loggedIn[0] = true;
//            }
//
//            @Override
//            public void loginFail(String reason) {
//                System.out.print(reason);
//                done[0] = true;
//                loggedIn[0] = false;
//
//            }
//        });
//
//        Nimbits.instance.loginWithKey(base, locEmail, key);
//        while (!done[0]) {
//            Thread.sleep(10);
//        }
//        assertTrue(loggedIn[0]);
//
//        List<Entity> e = Transaction.getEntity(SimpleValue.getInstance(locEmail + "/" + p), EntityType.point, PointModel.class);
//        List<Entity> e2 = Transaction.getEntity(SimpleValue.getInstance(locEmail + "/" + p2), EntityType.point, PointModel.class);
//        Entity calc1 = e2.get(0);
//        assertFalse(e.isEmpty());
//        System.out.print(e.get(0).getUUID());
//        double v = 0.001;
//        for (int i = 0; i < 10; i++) {
//            Transaction.postValue(e.get(0), ValueFactory.createValueModel(v));
//            Thread.sleep(1000);
//            List<Value> cx = Transaction.getValue(calc1);
//            assertEquals(cx.get(0).getDoubleValue(), v,0.01);
//            assertFalse(cx.isEmpty());
//
//            v += 0.001;
//        }
//        List<Value> vx = Transaction.getSeries(e.get(0), Range.between(0, 100));
//        long lastts = 0;
//        assertFalse(vx.isEmpty());
//        for (Value vr : vx) { //test data comes back in ascending order
//            assertTrue(lastts < vr.getTimestamp().getTime());
//            lastts = vr.getTimestamp().getTime();

        }

        //Transaction.postValue(e.get(0), ValueFactory.createValueModel(0.1725));
//
//        List<Value> vx = Transaction.getSeries(e.get(0), Range.between(0, 100));
//        assertFalse(vx.isEmpty());
//        for (Value v : vx) {
//            System.out.println(v.getDoubleValue());
//        }
  //  }

}
