package com.nimbits.server.dao;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.device.Device;
import com.nimbits.cloudplatform.client.model.device.DeviceImpl;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.trigger.Trigger;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.shared.Utils;
import com.nimbits.server.orm.DeviceEntity;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
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
