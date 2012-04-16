package com.nimbits.client.model.accesskey;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 2:24 PM
 */
public class AccessKeyFactory {

    private AccessKeyFactory() {
    }

    public static AccessKey createAccessKey(AccessKey key) throws NimbitsException {

        return new AccessKeyModel(key);

    }

    public static AccessKey createAccessKey(Entity en, String key, String source) throws NimbitsException {
        return new AccessKeyModel(en, key, source);
    }
}
