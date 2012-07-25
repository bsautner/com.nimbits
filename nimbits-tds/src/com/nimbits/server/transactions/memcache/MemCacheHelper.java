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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.memcache;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/22/11
 * Time: 12:19 PM
 */
public class MemCacheHelper {
     private static final String CACHE_KEY_PREFIX = "FOO";






    public static final String allUsersCacheKey =  CACHE_KEY_PREFIX + Parameters.user.getText() + SettingType.serverVersion.getDefaultValue() + "ALLUSERS";

    public static String UserCacheKey(final EmailAddress emailAddress) {
        return  CACHE_KEY_PREFIX + Parameters.user.getText() + SettingType.serverVersion.getDefaultValue()+    emailAddress.getValue();
    }


    public static String UserCacheKey(final User u) throws NimbitsException {
        return CACHE_KEY_PREFIX + Parameters.user.getText() + SettingType.serverVersion.getDefaultValue()+   u.getEmail().getValue();
    }

    public static String UserCacheKey(final String id) {
        return  CACHE_KEY_PREFIX + Parameters.user.getText() + SettingType.serverVersion.getDefaultValue() +   id;
    }



}
