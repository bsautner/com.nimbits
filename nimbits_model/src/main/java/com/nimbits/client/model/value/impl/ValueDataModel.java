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

import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.value.ValueData;

import java.io.Serializable;


public class ValueDataModel implements ValueData, Serializable {

    private String data;

    protected ValueDataModel() {
        this.data = "";
    }

    public ValueDataModel(SimpleValue<String> data) {
        this.data = data.getValue();
    }

    public static ValueData getInstance(SimpleValue<String> data) {
       return  new ValueDataModel(data);

    }
    public static ValueData getEmptyInstance( ) {
        return  new ValueDataModel();


    }
    @Override
    public String getContent() {
        return this.data;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueDataModel that = (ValueDataModel) o;
        if (data==null) {
            data = "";
        }
        if (!data.equals(that.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
