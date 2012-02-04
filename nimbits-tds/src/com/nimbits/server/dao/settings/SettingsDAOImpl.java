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

import com.nimbits.PMF;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.server.orm.ServerSetting;
import com.nimbits.server.settings.SettingTransactions;
import com.nimbits.shared.Utils;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.*;
import java.util.logging.Logger;

public class SettingsDAOImpl implements SettingTransactions {

    private static final Logger log = Logger.getLogger(SettingsDAOImpl.class.getName());

    private static String serverSecret = "";


    @Override
    public String getSetting(final String paramName) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        String retVal;
        try {
            ServerSetting s;
            Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            @SuppressWarnings(Const.WARNING_UNCHECKED)
            List<ServerSetting> a = (List<ServerSetting>) q.execute(paramName);
            if (a.size() > 0) {
                s = a.get(0);
                retVal = s.getValue();
            } else {
                throw new NimbitsException(paramName + new Date().toString() + " setting not found. "
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString()
                        + UUID.randomUUID().toString());

                // just in case someone tried to submit the error message as the
                // secret, we generate a random return string with the error.

            }
        } finally {
            pm.close();
        }

        return retVal;
    }

    @Override
    public void updateSetting(final String name, final String newValue) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            ServerSetting s;

            Query q = pm.newQuery(ServerSetting.class, "name == n");
            q.setRange(0, 1);
            q.declareParameters("String n");
            @SuppressWarnings(Const.WARNING_UNCHECKED)
            List<ServerSetting> a = (List<ServerSetting>) q.execute(name);
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
    public Map<String, String> getSettings() {
        final Map<String, String> settings = new HashMap<String, String>();
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(ServerSetting.class);
            final List<ServerSetting> l = (List<ServerSetting>) q.execute();

            for (final ServerSetting s : l) {
                settings.put(s.getName(), s.getValue());
            }
            return settings;
        }finally {
            pm.close();
        }




    }

    @Override
    public void addSetting(final String name, final String value) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            ServerSetting s = new ServerSetting();
            s.setName(name);
            s.setValue(value);
            pm.makePersistent(s);
        } finally {
            pm.close();
        }


    }

    @Override
    public String getServerSecret() {
        String retVal = UUID.randomUUID() + Const.MESSAGE_SERVER_SECRET_ERROR;


        if (!Utils.isEmptyString(serverSecret)) {
            retVal = serverSecret;
        } else {

            final PersistenceManager pm = PMF.get().getPersistenceManager();

            try {
                ServerSetting s;
                Query q = pm.newQuery(ServerSetting.class, "name == n");
                q.setRange(0, 1);
                q.declareParameters("String n");
                @SuppressWarnings(Const.WARNING_UNCHECKED)
                List<ServerSetting> a = (List<ServerSetting>) q.execute(Const.PARAM_SECRET);
                if (a.size() > 0) {
                    s = a.get(0);
                    retVal = s.getValue();
                    if (!Utils.isEmptyString(retVal)) {
                        serverSecret = retVal;
                    }
                } else {
                    // just in case someone tried to submit the error message as the
                    // secret, we generate a random return string with the error.
                    retVal = UUID.randomUUID() + Const.MESSAGE_SERVER_SECRET_ERROR;
                }

            }  finally {
                pm.close();
            }

        }

        return retVal;

    }
}
