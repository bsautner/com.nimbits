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

package com.nimbits.server.dao;

import com.nimbits.client.model.device.Device;
import com.nimbits.client.model.device.DeviceImpl;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.orm.DeviceEntity;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.*;

/**
 * Created by benjamin on 7/23/13.
 */
public class Transactions {

    public static final int LIMIT = 10;
    private static PersistenceManagerFactory pmf;

    static {
        pmf = PMF.get();
    }

    public static void saveDevice(Device device) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            if (! isDeviceRegistered(device)) {
                DeviceEntity commit = new DeviceEntity(device);
                pm.makePersistent(commit);
            }
        } finally {
            pm.close();
        }
    }




    public static List<Device> getDevices(final EmailAddress user) {

        final PersistenceManager pm = pmf.getPersistenceManager();
        final List<Device> retObj = new ArrayList<Device>(LIMIT);
        try {

            final Query q = pm.newQuery(DeviceEntity.class, "owner == o");
            q.setRange(0, LIMIT);
            q.declareParameters("String o");
            final List<DeviceEntity> r = (List<DeviceEntity>) q.execute(user.getValue());
            for (DeviceEntity e : r) {
                retObj.add(new DeviceImpl(e));
            }

        } finally {
            pm.close();
        }
        return retObj;

    }


    public static boolean isDeviceRegistered(final Device device) {

        final PersistenceManager pm = pmf.getPersistenceManager();

        try {

            final Query q = pm.newQuery(DeviceEntity.class, "regId == o");
            q.setRange(0, 1);
            q.declareParameters("String o");
            final List<DeviceEntity> r = (List<DeviceEntity>) q.execute(device.getRegId());
            return ! r.isEmpty();

        } finally {
            pm.close();
        }


    }
}
