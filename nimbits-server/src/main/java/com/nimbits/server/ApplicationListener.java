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

import com.nimbits.PMF;
import com.nimbits.server.cache.CacheFactory;
import com.nimbits.server.communication.email.EmailService;
import com.nimbits.server.communication.email.EmailServiceFactory;
import com.nimbits.server.communication.xmpp.XmppService;
import com.nimbits.server.communication.xmpp.XmppServiceFactory;
import com.nimbits.server.counter.CounterService;
import com.nimbits.server.io.blob.BlobStore;
import com.nimbits.server.io.blob.BlobStoreFactory;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.TaskServiceFactory;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.user.service.AuthenticationMechanism;
import com.nimbits.server.transactions.counter.CounterServiceFactory;
import com.nimbits.server.transactions.user.AuthenticationMechanismFactory;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class ApplicationListener implements ServletContextListener {
    private static NimbitsEngine engine;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();

        context.setAttribute("engine", createEngine());
        context.setAttribute("task", getTaskService(engine));
    }

    public static TaskService getTaskService(NimbitsEngine engine) {
        TaskService taskService = TaskServiceFactory.getServiceInstance(engine);
        return taskService;
    }

    public static NimbitsEngine createEngine() {

        if (engine == null) {
            PersistenceManagerFactory persistenceManagerFactory = PMF.get();
            NimbitsCache cache = CacheFactory.getInstance();
            XmppService xmppService = XmppServiceFactory.getServiceInstance();
            BlobStore blobStore = BlobStoreFactory.getInstance(persistenceManagerFactory);
            AuthenticationMechanism userAuthenticationMechanism = AuthenticationMechanismFactory.getInstance();
            EmailService emailService = EmailServiceFactory.getServiceInstance(persistenceManagerFactory);
            CounterService counterService = CounterServiceFactory.getInstance();
            engine = new NimbitsEngine(
                    persistenceManagerFactory,
                    cache,
                    blobStore,
                    xmppService,
                    userAuthenticationMechanism,
                    counterService,
                    emailService);
        }
        return engine;
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
