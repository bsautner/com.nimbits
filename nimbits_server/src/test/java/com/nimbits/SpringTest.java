/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
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

package com.nimbits;

import com.nimbits.server.transaction.entity.cache.EntityCacheImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"applicationContext.xml", "/META-INF/context.xml"})
public class SpringTest {

    @Autowired
    EntityCacheImpl entityCache;

    @Test
    public void testSpring() {
        assertTrue(true);
        assertNotNull(entityCache);
        assertNotNull(entityCache.getCache());

    }
}
