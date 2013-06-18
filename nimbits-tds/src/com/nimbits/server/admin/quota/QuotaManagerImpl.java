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

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.timespan.TimespanService;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:27 PM
 */
@Component("quotaManager")
public class QuotaManagerImpl implements QuotaManager {



    public static final int FREE_DAILY_QUOTA = 1000;
    public static final double COST_PER_API_CALL = 0.00001;
    private MemcacheService cacheFactory;
    private TimespanService timespanService;


    @Override
    public void updateUserStatusGrid(final User user, final int count) throws NimbitsException {
        Map<EmailAddress, User> map;

        if (cacheFactory.contains(MemCacheKey.userReport)) {
            map = (Map<EmailAddress, User>) cacheFactory.get(MemCacheKey.userReport);
        }
        else {
            map = new HashMap<EmailAddress, User>(100);
        }
        user.setApiCount(count);
        user.setLastLoggedIn(new Date());
        map.put(user.getEmail(), user);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 30);

        cacheFactory.put(MemCacheKey.userReport, map, Expiration.onDate(c.getTime()), MemcacheService.SetPolicy.SET_ALWAYS);



    }

    @Override
    public Map<EmailAddress, User> getUserStatusGrid() {
        if (cacheFactory.contains(MemCacheKey.userReport)) {
            return  (Map<EmailAddress, User>) cacheFactory.get(MemCacheKey.userReport);
        }
        else {
            return new HashMap<EmailAddress, User>(0);
        }

    }


    @Override
    public int getFreeDailyQuota() {
        return FREE_DAILY_QUOTA;
    }

    private String getKey(final EmailAddress email) {
        return  MemCacheKey.getKey(MemCacheKey.quota, email.getValue());


    }

    @Override
    public int incrementCounter(final EmailAddress email) throws NimbitsException {
        Date midnight = timespanService.zeroOutDateToEnd(new Date());
        if (cacheFactory.contains(getKey(email)))  {
            int v = (Integer) cacheFactory.get(getKey(email));
            v +=1;
            cacheFactory.put(getKey(email), v,Expiration.onDate(midnight), MemcacheService.SetPolicy.SET_ALWAYS);
            return v;

        }
        else {


            cacheFactory.put(getKey(email), 1, Expiration.onDate(midnight), MemcacheService.SetPolicy.SET_ALWAYS);
            return 1;
        }


    }
    @Override
    public double getCostPerApiCall() {
        return COST_PER_API_CALL;
    }

//    @Override
//    public void resetCounter(final EmailAddress email) throws NimbitsException {
//        initCache(email);
//        cacheFactory.delete(key);
//    }
//    @Override
//    public void resetCounters() throws NimbitsException {
//        initCache(null);
//
//        cache.clearAll();
//    }

    @Override
    public int getCount(final EmailAddress email) throws NimbitsException {

        if (cacheFactory.contains(getKey(email)))  {
            return (Integer)cacheFactory.get(getKey(email));
        }
        else {
            return 0;
        }
    }


    public void setCacheFactory(MemcacheService cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    public void setTimespanService(TimespanService timespanService) {
        this.timespanService = timespanService;
    }
}
