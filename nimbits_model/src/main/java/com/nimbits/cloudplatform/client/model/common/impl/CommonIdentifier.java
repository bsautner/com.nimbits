/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.cloudplatform.client.model.common.impl;
import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/6/11
 * Time: 11:07 AM
 */
public class CommonIdentifier implements  Serializable{

    private String value;

    protected CommonIdentifier(final String value) {
        this.value = value;
    }


    public String getValue() {
        return value;

    }


    public String toString() {
        return  this.value;
    }

    protected CommonIdentifier() {
         value = null;
    }


    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonIdentifier)) return false;

        final CommonIdentifier that = (CommonIdentifier) o;

        return value.equals(that.value);

    }


    public int hashCode() {
        return value.hashCode();
    }
}
