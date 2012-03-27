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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.memcache;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/22/11
 * Time: 12:19 PM
 */
public class MemCacheHelper {
    public static final String DEFAULT_CACHE_NAMESPACE = SettingType.serverVersion.getDefaultValue() + "DEFAULT";
    private static final String CACHE_KEY_PREFIX = "FOO";

    public static String currentValueCacheKey(String uuid) {
        return SettingType.serverVersion.getDefaultValue() + CACHE_KEY_PREFIX + "MOST_RECENT_VALUE_CACHE" + uuid;
    }

    public static String valueMemCacheNamespace(Point point) {
        return MemCacheKey.valueCache +  point.getUUID();
    }

    public static String valueBufferCacheKey(Point point) {
        return SettingType.serverVersion.getDefaultValue() +  CACHE_KEY_PREFIX + "BUFFERMEM" + point.getUUID();
    }

    public static String defaultPointCache() {
        return SettingType.serverVersion.getDefaultValue() + "DEFAULT_POINT_NAMESPACE";
    }



    public static String allUsersCacheKey =  CACHE_KEY_PREFIX + Params.PARAM_USER + SettingType.serverVersion.getDefaultValue() + "ALLUSERS";

    public static String UserCacheKey(final EmailAddress emailAddress) {
        return  CACHE_KEY_PREFIX + Params.PARAM_USER + SettingType.serverVersion.getDefaultValue()+    emailAddress.getValue();
    }


    public static String UserCacheKey(final User u) {
        return CACHE_KEY_PREFIX + Params.PARAM_USER + SettingType.serverVersion.getDefaultValue()+   u.getEmail().getValue();
    }

    public static String UserCacheKey(final long id) {
        return  CACHE_KEY_PREFIX + Params.PARAM_USER + SettingType.serverVersion.getDefaultValue() +   id;
    }
    public static String makeSafeNamespace(final String sample) {

       if (sample.matches(Const.REGEX_NAMESPACE)) {
          return sample;
       }
        else {
           return sample.replaceAll(Const.REGEX_SPECIAL_CHARS, "A");
       }
    }


}
