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

package com.nimbits.client.model.device;

import java.io.Serializable;


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
