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

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:27 PM
 */
public class QuotaImpl implements Quota {

    private final MemcacheService cache;
    private final String key;
    public static final int MAX_DAILY_QUOTA = 1000;
    public static final double COST_PER_API_CALL = 0.00001;
    @Override
    public int getMaxDailyQuota() {
        return MAX_DAILY_QUOTA;
    }

    public QuotaImpl() { //use email since sometimes we only have the key

        cache =MemcacheServiceFactory.getMemcacheService(MemCacheKey.quotaNamespace.getText());
        key = null;


    }

    public QuotaImpl(final EmailAddress email) { //use email since sometimes we only have the key

        cache =MemcacheServiceFactory.getMemcacheService(MemCacheKey.quotaNamespace.getText());
        key = MemCacheKey.getKey(MemCacheKey.quota, email.getValue());


    }

    @Override
    public int incrementCounter() throws NimbitsException {

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
    public void resetCounter() throws NimbitsException {
        cache.delete(key);
    }
    @Override
    public void resetCounters() throws NimbitsException {
        cache.clearAll();
    }
    @Override
    public int getCount() throws NimbitsException {
        if (cache.contains(key))  {
            return (Integer)cache.get(key);


        }
        else {
            return 0;
        }
    }



}
