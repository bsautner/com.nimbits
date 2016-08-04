/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server;

import com.nimbits.client.enums.ServerSetting;
import com.nimbits.server.transaction.settings.SettingsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Logger;

@Component
public class ApplicationListener implements ServletContextListener {

    private final Logger logger = Logger.getLogger(ApplicationListener.class.getName());

    @Autowired
    private SettingsDao settingsDao;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("contextInitialized");
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        String admin = settingsDao.getSetting(ServerSetting.admin);
        logger.info(String.format("system admin set: %b ", admin != null));

    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        ClassLoader applicationClassLoader = this.getClass().getClassLoader();
        Enumeration<Driver> driverEnumeration = DriverManager.getDrivers();
        while (driverEnumeration.hasMoreElements()) {
            Driver driver = driverEnumeration.nextElement();
            ClassLoader driverClassLoader = driver.getClass().getClassLoader();
            if (driverClassLoader != null
                    && driverClassLoader.equals(applicationClassLoader)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    e.printStackTrace(); //TODO Replace with your exception handling
                }
            }
        }
    }



}
