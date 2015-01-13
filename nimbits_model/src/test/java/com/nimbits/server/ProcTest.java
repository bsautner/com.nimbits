package com.nimbits.server;


import org.junit.*;

import static org.junit.Assert.assertNotNull;

public class ProcTest {



    @org.junit.Test
    public void testDependency() {
        Test test = new Test();
        assertNotNull(test);
        System.out.print(Test.FOO);
    }


}
