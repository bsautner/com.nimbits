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

package com.nimbits.server.transaction.settings;


import com.nimbits.client.enums.SettingType;
import com.nimbits.client.model.setting.Setting;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.orm.ServerSetting;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;


public class SettingsDaoImpl implements SettingsService {

    private PersistenceManagerFactory pmf;


    public SettingsDaoImpl(NimbitsEngine engine) {
        this.pmf = engine.getPmf();
    }

    @Override
    public String getSetting(final SettingType setting)  {
        final PersistenceManager pm = pmf.getPersistenceManager();
        String retVal;
        try {
            final Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<ServerSetting> a = (List<ServerSetting>) q.execute(setting.getName());
            if (a.isEmpty()) {
               return setting.getDefaultValue();

            } else {
                final Setting s = a.get(0);
                retVal = s.getValue();
            }
        } finally {
            pm.close();
        }

        return retVal;
    }
    @Override
    public void updateSetting(final SettingType name, final String newValue) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {

            final Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<Setting> a = (List<Setting>) q.execute(name.getName() );
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



    @Override
    public void addSetting(final SettingType name, final String value) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Setting s = new ServerSetting(name, value);
            pm.makePersistent(s);
        } finally {
            pm.close();
        }


    }


}
