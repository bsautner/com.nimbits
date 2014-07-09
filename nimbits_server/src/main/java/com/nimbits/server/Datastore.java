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

public class Datastore {
    private static PersistenceManagerFactory PMF;

    public static void initialize() {
        if (PMF != null) {
            throw new IllegalStateException("initialize() already called");
        }
        Properties props = new Properties();
        props.setProperty("javax.jdo.PersistenceManagerFactoryClass",
                "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("datanucleus.PersistenceUnitName", "pmf");
        PMF = JDOHelper.getPersistenceManagerFactory(props);

    }

    public static PersistenceManagerFactory get() {
        if (PMF == null) {
            initialize();
        }

        return PMF;
    }


}
