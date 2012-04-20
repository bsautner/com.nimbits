/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.admin.settings;


import com.nimbits.server.transactions.dao.settings.*;
import com.nimbits.server.transactions.memcache.settings.*;

public class SettingTransactionsFactory {
    private SettingTransactionsFactory() {
    }


    private static class SettingTransactionsHolder {
        static final SettingTransactions daoInstance = new SettingsDAOImpl();
        static final SettingTransactions memInstance = new SettingMemCacheImpl();
        private SettingTransactionsHolder() {
        }
    }

    public static SettingTransactions getInstance() {
        return SettingTransactionsHolder.memInstance;
    }

    public static SettingTransactions getDaoInstance() {

        return SettingTransactionsHolder.daoInstance;
    }
}


