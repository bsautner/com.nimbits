/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class PMF {
    private static final String PMF_TRANSACTIONS_OPTIONAL = "transactions-optional";

    private PMF() {
    }

    private static class PersistenceManagerFactoryHolder {
        static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory(PMF_TRANSACTIONS_OPTIONAL);

        private PersistenceManagerFactoryHolder() {
        }
    }

    public static PersistenceManagerFactory get() {
        return PersistenceManagerFactoryHolder.pmfInstance;
    }

}