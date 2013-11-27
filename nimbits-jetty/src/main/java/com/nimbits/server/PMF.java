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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.util.Properties;

/**
 * Created by benjamin on 10/11/13.
 */
public class PMF {

    private static PersistenceManagerFactory instance;

    public static PersistenceManagerFactory get() {

        if (instance == null) {
            Properties props = new Properties();
            props.setProperty("javax.jdo.PersistenceManagerFactoryClass",
                    "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
            props.put("datanucleus.PersistenceUnitName", "pmf");
            instance = JDOHelper.getPersistenceManagerFactory(props);
        }
        return instance;
    }
}
