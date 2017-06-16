package com.nimbits.it.basic;/*
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
import com.nimbits.client.model.Filter;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

public class V3FilterTestAbstract extends AbstractNimbitsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();


    }

    @Test
    public void fixedHysteresisTest() throws InterruptedException {


        Topic topic = nimbits.addPoint(user, new Topic.Builder()

                .create());

        Filter filter = nimbits.addFilter(topic, new Filter.Builder()
                .filterValue(100)
                .filterType(FilterType.fixedHysteresis)
                .trigger(topic)
                .create());

        if (topic == null) {
            throw new RuntimeException("Test Failed!");
        }

        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            log("recording random string data");
            nimbits.recordValue(topic, new Value.Builder().data(UUID.randomUUID().toString()).create());
        }

        for (int i = 0; i < 100; i++) {
            log("recording random double data");
            nimbits.recordValue(topic, new Value.Builder().doubleValue(r.nextDouble()).data(UUID.randomUUID().toString()).create());
        }

        log("Done!");


    }
}
