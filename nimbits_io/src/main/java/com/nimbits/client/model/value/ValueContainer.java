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

package com.nimbits.client.model.value;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ValueContainer implements Serializable {

    @Expose
    private String owner;

    @Expose
    private String id;

    @Expose
    private Value value;

    @SuppressWarnings("unused")
    public ValueContainer() {

    }

    public ValueContainer(String owner, String id, Value value) {
        this.owner = owner;
        this.id = id;
        this.value = value;
    }

    
    public String getOwner() {
        return owner;
    }

    
    public String getId() {
        return id;
    }

    
    public Value getValue() {
        return value;
    }

    
    public void setValue(Value value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueContainer)) return false;

        ValueContainer that = (ValueContainer) o;

        if (!owner.equals(that.owner)) return false;
        if (!id.equals(that.id)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
