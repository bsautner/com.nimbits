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
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.util.Properties;


public final class Datastore {

//    private static final PersistenceManagerFactory pmfInstance =
//            JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private static PersistenceManagerFactory PMF;
    private static final ThreadLocal<PersistenceManager> PER_THREAD_PM = new ThreadLocal<PersistenceManager>();

    public static void initialize() {
        if (PMF != null) {
            throw new IllegalStateException("initialize() already called");
        }

        PMF =  JDOHelper.getPersistenceManagerFactory("transactions-optional");

    }
    public static void delete() {
       PMF = null;
    }
    public static PersistenceManager getPersistenceManager() {
        PersistenceManager pm = PER_THREAD_PM.get();
        if (pm == null) {
            pm = PMF.getPersistenceManager();
            PER_THREAD_PM.set(pm);
        }
        return pm;
    }

    public static void finishRequest() {
        PersistenceManager pm = PER_THREAD_PM.get();
        if (pm != null) {
            PER_THREAD_PM.remove();
            Transaction tx = pm.currentTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }

    }


}
