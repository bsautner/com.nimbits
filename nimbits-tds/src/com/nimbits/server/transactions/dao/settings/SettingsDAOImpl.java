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

package com.nimbits.server.transactions.dao.settings;

import com.nimbits.PMF;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.setting.Setting;
import com.nimbits.server.orm.ServerSetting;
import com.nimbits.server.settings.SettingTransactions;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
@Repository("settingsDao")
public class SettingsDAOImpl implements SettingTransactions {


    @Override
    public String getSetting(final SettingType setting) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        String retVal;
        try {
            final Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<ServerSetting> a = (List<ServerSetting>) q.execute(setting.getName());
            if (a.isEmpty()) {
                throw new NimbitsException(setting.getName() + " setting not found.");
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
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {

            final Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            final List<Setting> a = (List<Setting>) q.execute(name.getName());
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
    public String reloadCache() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public Map<SettingType, String> getSettings() {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(ServerSetting.class);
            final Iterable<ServerSetting> l = (Iterable<ServerSetting>) q.execute();
            final Map<SettingType, String> settings = new HashMap<SettingType, String>(SettingType.values().length);
            for (final Setting s : l) {
                final SettingType type = s.getSetting();
                if (type != null) { //this can happen if an old setting in the db isn't in the enum anymore
                settings.put(s.getSetting(), s.getValue());
                }
            }
            return settings;
        }finally {
            pm.close();
        }

    }

    @Override
    public void addSetting(final SettingType name, final String value) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Setting s = new ServerSetting(value, name);
            pm.makePersistent(s);
        } finally {
            pm.close();
        }


    }


}
