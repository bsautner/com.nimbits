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

package com.nimbits.client.model.value.impl;

import com.nimbits.client.model.value.ValueContainer;
import com.nimbits.client.model.value.Value;

public class ValueContainerModel implements ValueContainer {

     String owner;

     String id;

     String accessKey;

     Value value;

    @SuppressWarnings("unused")
    public ValueContainerModel() {

    }

    public ValueContainerModel(String owner, String id, String accessKey, Value value) {
        this.owner = owner;
        this.id = id;
        this.value = value;
        this.accessKey = accessKey;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Value  getValue() {
        return value;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueContainerModel that = (ValueContainerModel) o;

        if (accessKey != null ? !accessKey.equals(that.accessKey) : that.accessKey != null) return false;
        if (!id.equals(that.id)) return false;
        if (!owner.equals(that.owner)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (accessKey != null ? accessKey.hashCode() : 0);
        result = 31 * result + value.hashCode();
        return result;
    }
}
