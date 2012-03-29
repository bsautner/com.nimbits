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

package com.nimbits.server.dao.settings;

import com.nimbits.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.settings.*;

import javax.jdo.*;
import java.util.*;


@SuppressWarnings("unchecked")
public class SettingsDAOImpl implements SettingTransactions {


    @Override
    public String getSetting(final SettingType setting) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        String retVal;
        try {
            ServerSetting s;
            Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            List<ServerSetting> a = (List<ServerSetting>) q.execute(setting.getName());
            if (a.size() > 0) {
                s = a.get(0);
                retVal = s.getValue();
            }
            else {
                throw new NimbitsException(setting.getName() + " setting not found.");
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
            ServerSetting s;

            Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            List<ServerSetting> a = (List<ServerSetting>) q.execute(name.getName());
            if (a.size() > 0) {
                Transaction tx = pm.currentTransaction();
                tx.begin();
                s = a.get(0);
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
        final Map<SettingType, String> settings = new HashMap<SettingType, String>();
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(ServerSetting.class);
            final List<ServerSetting> l = (List<ServerSetting>) q.execute();

            for (final ServerSetting s : l) {
                settings.put(s.getSetting(), s.getValue());
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
            ServerSetting s = new ServerSetting(value, name);
            pm.makePersistent(s);
        } finally {
            pm.close();
        }


    }


}
