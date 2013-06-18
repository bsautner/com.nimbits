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

import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.email.impl.EmailAddressImpl;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.entity.EntityNameImpl;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/6/11
 * Time: 11:09 AM
 */
public class CommonFactory {


    public static EmailAddress createEmailAddress(final String value) {
        emailTest(value);
        if (value == null) {
            throw new IllegalArgumentException("Email can not be null");
        }
        return new EmailAddressImpl(value);

    }

    protected static void emailTest(final String value) {
        if (Utils.isEmptyString(value)) {
            throw new IllegalArgumentException("Email was empty");
        }
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Email seems invalid");
        }
        if (!value.contains(".")) {
            throw new IllegalArgumentException("Email seems invalid");
        }
    }


    public static EntityName createName(final String name, final EntityType type) {
        nameTest(name, type);
        return new EntityNameImpl(name);
    }


    protected static void nameTest(final String name, final EntityType type) {


        if (Utils.isEmptyString(name)) {
            throw new IllegalArgumentException("Invalid Empty Name");

        }

        if (name.contains("%")
                || name.contains("+")
                || name.contains("'")
                || name.contains("\"")
                || name.contains("!")
                || name.contains("?")
                ) {
            throw new IllegalArgumentException("Invalid Name");
        }


        if (name.length() > Const.CONST_MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Whoa! That's a long name. Names must be less than " + Const.CONST_MAX_NAME_LENGTH + " chars!");

        }

        switch (type) {

            case user:
                if (!name.contains("@")) {
                    throw new IllegalArgumentException("Invalid Name");
                }
                break;
            case point:
                break;
            case category:
                break;
            case subscription:
                break;
            case calculation:
                break;
            default:
                break;
        }
    }


}
