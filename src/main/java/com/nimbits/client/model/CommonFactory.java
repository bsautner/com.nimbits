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

package com.nimbits.client.model;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import org.apache.commons.lang3.StringUtils;


public class CommonFactory {


    public static String createEmailAddress(final String value) {
        emailTest(value);
        if (value == null) {
            throw new IllegalArgumentException("Email can not be null");
        }
        return value.toLowerCase();

    }

    private static void emailTest(final String value) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Email was empty");
        }
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Email seems invalid");
        }
        if (!value.contains(".")) {
            throw new IllegalArgumentException("Email seems invalid");
        }
    }


    public static String createName(final String name, final EntityType type) {
        nameTest(name, type);
        return name;
    }


    private static void nameTest(final String name, final EntityType type) {


        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Invalid Empty Name");

        }

        if (name.contains("%")
                || name.contains("'")
                || name.contains("\"")
                || name.contains("!")
                || name.contains("?")
                ) {
            throw new IllegalArgumentException("Invalid Name: " + name);
        }


        if (name.length() > Const.CONST_MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Entity Names must be less than " + Const.CONST_MAX_NAME_LENGTH + " chars!");

        }

        switch (type) {

            case user:
                if (!name.contains("@")) {
                    throw new IllegalArgumentException(String.format("Invalid Email Format: %s", name));
                }
                break;
            case topic:
                break;
            case group:
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
