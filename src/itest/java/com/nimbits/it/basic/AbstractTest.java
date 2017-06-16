package com.nimbits.it.basic;

import java.util.Date;

import static org.junit.Assert.fail;

public abstract class AbstractTest {

    protected static void log(Object... msg) {
        StringBuilder sb = new StringBuilder();
        for (Object o : msg) {
            sb.append("-> ").append(String.valueOf(o)).append("\n");
        }
        System.out.println(String.format("%s %s", new Date(), sb.toString()));
    }



    protected void sleep() {
        try {
            log("sleeping...");
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {

        }
    }

    protected void nap() {
        try {
            log("napping...");
            Thread.sleep(500);
        } catch (InterruptedException ignored) {

        }
    }

    protected void sleep(int i) {
        log("big sleep " + i);
        for (int c = 0; c < i; c++) {
            sleep();
        }
    }

    public void error(String message) {
        log("Error: " + message);

        fail();
    }
}
