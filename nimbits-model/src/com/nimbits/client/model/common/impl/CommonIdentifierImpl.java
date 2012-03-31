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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.common.impl;

import com.nimbits.client.model.common.CommonIdentifier;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/6/11
 * Time: 11:07 AM
 */
public abstract class CommonIdentifierImpl implements CommonIdentifier, Serializable{

    private final String value;

    protected CommonIdentifierImpl(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;

    }

    @Override
    public String toString() {
        return  this.value;
    }

    protected CommonIdentifierImpl() {
         value = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonIdentifierImpl)) return false;

        CommonIdentifierImpl that = (CommonIdentifierImpl) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
