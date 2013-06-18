package com.nimbits.cloudplatform.client.model.accesskey;

import com.nimbits.cloudplatform.client.enums.AuthLevel;
import com.nimbits.cloudplatform.client.model.entity.Entity;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 2:24 PM
 */
public class AccessKeyFactory {

    private AccessKeyFactory() {
    }

    public static AccessKey createAccessKey(AccessKey key)  {

        return new AccessKeyModel(key);

    }

    public static AccessKey createAccessKey(Entity en, String code, String scope, AuthLevel level)  {
        return new AccessKeyModel(en, code, scope, level);
    }
}
