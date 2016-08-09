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

package com.nimbits.server.transaction.settings;


import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.setting.Setting;
import com.nimbits.server.orm.SettingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;

@Repository
public class SettingsDao {

    private PersistenceManagerFactory persistenceManagerFactory;

    @Autowired
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    public SettingsDao() {

    }

    @Cacheable(cacheNames = "settings", key = "#setting.name")
    public String getSetting(final ServerSetting setting) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        String retVal;
        try {
            final Query q = pm.newQuery(SettingEntity.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<SettingEntity> a = (List<SettingEntity>) q.execute(setting.getName());
            if (a.isEmpty()) {
                return setting.getDefaultValue();

            } else {
                final Setting s = a.get(0);

                retVal = s.getValue() == null ? setting.getDefaultValue() : s.getValue();
            }
        } finally {
            pm.close();
        }

        return retVal;
    }

    @CacheEvict(cacheNames = "settings", key = "#setting.name")
    public void updateSetting(final ServerSetting setting, final String newValue) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {

            final Query q = pm.newQuery(SettingEntity.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<Setting> a = (List<Setting>) q.execute(setting.getName());
            if (!a.isEmpty()) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                final Setting s = a.get(0);
                s.setValue(newValue);
                tx.commit();


            } else {
                SettingEntity newSetting = new SettingEntity(setting, newValue);
                pm.makePersistent(newSetting);
            }
        } finally {
            pm.close();
        }
    }




}
