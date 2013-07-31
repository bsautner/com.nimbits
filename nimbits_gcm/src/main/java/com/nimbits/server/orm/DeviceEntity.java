package com.nimbits.server.orm;

import com.google.appengine.api.datastore.Key;
import com.nimbits.cloudplatform.client.model.device.Device;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Created by benjamin on 7/23/13.
 */

@PersistenceCapable
public class DeviceEntity implements Device {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;

    @Persistent
    private String regId;

    @Persistent
    private String owner;

    public DeviceEntity(String regId, String owner) {
        this.regId = regId;
        this.owner = owner;
    }

    public DeviceEntity(Device device) {
        this.regId = device.getRegId();
        this.owner = device.getOwner();
    }

    @Override
    public String getRegId() {
        return regId;
    }

    @Override
    public String getOwner() {
        return owner;
    }
}
