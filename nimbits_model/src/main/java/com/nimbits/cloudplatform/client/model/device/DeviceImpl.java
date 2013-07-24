package com.nimbits.cloudplatform.client.model.device;

import java.io.Serializable;

/**
 * Created by benjamin on 7/23/13.
 */
public class DeviceImpl implements Device, Serializable {

    private String regId;

    private String owner;

    public DeviceImpl(final String regId, final String owner) {
        this.regId = regId;
        this.owner = owner;
    }

    public DeviceImpl() {
    }

    public DeviceImpl(Device e) {
        this.regId = e.getRegId();
        this.owner = e.getOwner();
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
