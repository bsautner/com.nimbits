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

package com.nimbits.server;

import com.nimbits.server.api.*;
import com.nimbits.server.process.cron.IdlePointCron;
import com.nimbits.server.process.task.PointMaintTask;
import com.nimbits.server.process.task.ValueTask;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertTrue;


public class BaseTest {

    public static BatchApi batchApi;
    public  static ValueApi valueApi;
    public static SessionApi sessionApi;
    public static EntityApi entityApi;

    public static SeriesApi seriesApi;
    public  static ValueTask valueTask;
    public  static IdlePointCron idleCron;
    public  static PointMaintTask pointTask;

    @AfterClass
    public static void tearDown() {

    }
    @BeforeClass
    public static void before() {
        final MockServletContext context = new MockServletContext();
        System.setProperty("appengine.orm.disable.duplicate.pmf.exception", "false");


        NimbitsEngine engine = ApplicationListener.createEngine();

        context.setAttribute("engine", engine);
        context.setAttribute("task", ApplicationListener.getTaskService(engine));

        batchApi = new BatchApi() {

            @Override
            public ServletContext getServletContext() {

                return context;

            }

        };

        sessionApi = new SessionApi() {
            @Override
            public ServletContext getServletContext() {

                return context;

            }
        };

        entityApi = new EntityApi() {
            @Override
            public ServletContext getServletContext() {

                return context;

            }
        };



        seriesApi = new SeriesApi() {
            @Override
            public ServletContext getServletContext() {

                return context;

            }
        };

        valueApi = new ValueApi() {
            @Override
            public ServletContext getServletContext() {

                return context;

            }
        };

        valueTask = new ValueTask() {
            @Override
            public ServletContext getServletContext() {

                return context;

            }
        };

        pointTask = new PointMaintTask() {
            @Override
            public ServletContext getServletContext() {

                return context;

            }
        };


        idleCron = new IdlePointCron() {
            @Override
            public ServletContext getServletContext() {

                return context;

            }
        };
    }

    @Test
    public void someTest() {
        assertTrue(true);
    }
}
