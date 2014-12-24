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

package com.nimbits;

import com.nimbits.server.api.*;
import com.nimbits.server.process.task.PointMaintTask;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.mock.web.MockServletContext;


import javax.servlet.ServletContext;


public class BaseTest {

    public static BatchApi batchApi;
    public static ValueApi valueApi;
    public static SessionApi sessionApi;
    public static EntityApi entityApi;

    public static SeriesApi seriesApi;

    public static PointMaintTask pointTask;

    @AfterClass
    public static void tearDown() {

    }

    @BeforeClass
    public static void before() {
        final MockServletContext context = new MockServletContext();
        System.setProperty("appengine.orm.disable.duplicate.pmf.exception", "false");





    }
}
