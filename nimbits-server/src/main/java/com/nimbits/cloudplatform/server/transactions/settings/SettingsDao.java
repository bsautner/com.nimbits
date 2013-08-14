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

package com.nimbits.cloudplatform.server.transactions.settings;

import com.nimbits.cloudplatform.PMF;
import com.nimbits.cloudplatform.client.enums.SettingType;
import com.nimbits.cloudplatform.client.model.setting.Setting;
import com.nimbits.cloudplatform.server.orm.ServerSetting;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
@Repository("settingsDao")
public class SettingsDao {


    private static PersistenceManagerFactory pmf;

    static {
        pmf = PMF.get();

    }


    
    protected static  String getSetting(final String setting)  {
        final PersistenceManager pm = pmf.getPersistenceManager();
        String retVal;
        try {
            final Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<ServerSetting> a = (List<ServerSetting>) q.execute(setting);
            if (a.isEmpty()) {
                throw new IllegalArgumentException(setting + " setting not found. If this is a new install or upgrade, you " +
                        "may need to manually run /cron/systemCron before using this instance to initialize the platform.");
            } else {
                final Setting s = a.get(0);
                retVal = s.getValue();
            }
        } finally {
            pm.close();
        }

        return retVal;
    }

    
    protected static void updateSetting(final String name, final String newValue) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {

            final Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<Setting> a = (List<Setting>) q.execute(name );
            if (!a.isEmpty()) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                final Setting s =  a.get(0);
                s.setValue(newValue);
                tx.commit();

            }
        } finally {
            pm.close();
        }


    }

    
    protected static HashMap<String, String> getSettings() {

        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Query q = pm.newQuery(ServerSetting.class);
            final Iterable<ServerSetting> l = (Iterable<ServerSetting>) q.execute();
            final HashMap<String, String> settings = new HashMap<String, String>(SettingType.values().length);
            for (final Setting s : l) {
                final SettingType type = s.getSetting();
                if (type != null) { //this can happen if an old setting in the db isn't in the enum anymore
                settings.put(s.getSetting().getName(), s.getValue());
                }
            }
            return settings;
        }finally {
            pm.close();
        }

    }

    
    protected static void addSetting(final String name, final String value) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Setting s = new ServerSetting(value, name);
            pm.makePersistent(s);
        } finally {
            pm.close();
        }


    }


}
