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

package com.nimbits.server.admin.quota;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import org.springframework.stereotype.Component;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:27 PM
 */
@Component("quotaManager")
public class QuotaManagerImpl implements QuotaManager {

    private MemcacheService cache;
    private String key;
    public static final int FREE_DAILY_QUOTA = 1000;
    public static final double COST_PER_API_CALL = 0.00001;
    @Override
    public int getFreeDailyQuota() {
        return FREE_DAILY_QUOTA;
    }

//    public QuotaManagerImpl() { //use email since sometimes we only have the key
//

//
//
//    }

    private void initCache(final EmailAddress email) { //use email since sometimes we only have the key

        if (email != null) {
            cache =MemcacheServiceFactory.getMemcacheService(MemCacheKey.quotaNamespace.getText());
            key = MemCacheKey.getKey(MemCacheKey.quota, email.getValue());
        }
        else {
            cache =MemcacheServiceFactory.getMemcacheService(MemCacheKey.quotaNamespace.getText());
            key = null;
        }


    }

    @Override
    public int incrementCounter(final EmailAddress email) throws NimbitsException {
        initCache(email);
        if (cache.contains(key))  {
            int v = (Integer) cache.get(key);
            v +=1;
            cache.put(key, v);
            return v;

        }
        else {
            cache.put(key, 1);
            return 1;
        }


    }
    @Override
    public double getCostPerApiCall() {
        return COST_PER_API_CALL;
    }

    @Override
    public void resetCounter(final EmailAddress email) throws NimbitsException {
        initCache(email);
        cache.delete(key);
    }
    @Override
    public void resetCounters() throws NimbitsException {
        initCache(null);
        cache.clearAll();
    }

    @Override
    public int getCount(final EmailAddress email) throws NimbitsException {
        initCache(email);
        if (cache.contains(key))  {
            return (Integer)cache.get(key);
        }
        else {
            return 0;
        }
    }



}
