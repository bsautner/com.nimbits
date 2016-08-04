package com.nimbits.it;/*
 * NIMBITS INC CONFIDENTIAL
 * Copyrite (c) 2015 Nimbits Inc
 *  __________________
 *
 * [2013] - [2015] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
 */

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

public class V3FilterTest extends NimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void fixedHysteresisTest() throws InterruptedException {


        Point point = nimbits.addPoint(user, new PointModel.Builder()
                .filterType(FilterType.fixedHysteresis)
                .filterValue(100)

                .create());
        if (point == null) {
            throw new RuntimeException("Test Failed!");
        }

        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            log("recording random string data");
            nimbits.recordValue(point, new Value.Builder().data(UUID.randomUUID().toString()).create());
        }

        for (int i = 0; i < 100; i++) {
            log("recording random double data");
            nimbits.recordValue(point, new Value.Builder().doubleValue(r.nextDouble()).data(UUID.randomUUID().toString()).create());
        }

        log("Done!");


    }
}
