package com.nimbits.server.memcache;

import com.nimbits.client.model.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/22/11
 * Time: 12:19 PM
 */
public class MemCacheHelper {

    public static String pointKey(final User u) {
        return Const.CACHE_KEY_USER_PREFIX + "Point" + Const.CONST_SERVER_VERSION + u.getEmail().getValue();

    }
    public static String pointListKey(final User u) {
        return Const.CACHE_KEY_USER_PREFIX + "PointList" + Const.CONST_SERVER_VERSION + u.getEmail().getValue();

    }
    public static String categoryKey(final User u) {
        return Const.CACHE_KEY_USER_PREFIX + "Category" + Const.CONST_SERVER_VERSION + u.getEmail().getValue();

    }
    public static String pointCacheKey(final User u) {
        return Const.CACHE_KEY_USER_PREFIX + "Points" + Const.CONST_SERVER_VERSION + u.getUuid();

    }
    public static String categoryCollection(final User u) {
        return Const.CACHE_KEY_CATEGORY_PREFIX + Const.CONST_SERVER_VERSION + u.getEmail().getValue();
    }

    public static String allUsersCacheKey = Const.CACHE_KEY_PREFIX + Const.PARAM_USER + Const.CONST_SERVER_VERSION + "ALLUSERS";

    public static String UserCacheKey(final EmailAddress emailAddress) {
        return Const.CACHE_KEY_PREFIX + Const.PARAM_USER + Const.CONST_SERVER_VERSION +    emailAddress.getValue();
    }


    public static String UserCacheKey(final User u) {
        return Const.CACHE_KEY_PREFIX + Const.PARAM_USER + Const.CONST_SERVER_VERSION +   u.getEmail().getValue();
    }

    public static String UserCacheKey(final long id) {
        return Const.CACHE_KEY_PREFIX + Const.PARAM_USER + Const.CONST_SERVER_VERSION +   id;
    }
    public static String makeSafeNamespace(final String sample) {

       if (sample.matches(Const.REGEX_NAMESPACE)) {
          return sample;
       }
        else {
           String retStr = sample.replaceAll(Const.REGEX_SPECIAL_CHARS, "A");
           return retStr;
       }
    }


}
