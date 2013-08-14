/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.api.helper;


import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.location.Location;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import org.springframework.stereotype.Service;


import java.util.logging.Logger;
@Service("locationService")

public class LocationService {
  
    protected final static Logger log = Logger.getLogger(LocationService.class.getName());

    private static MemcacheService cacheFactory;

    static {
        cacheFactory = MemcacheServiceFactory.getMemcacheService();
    }



    public static void reportLocation(final Entity entity,final Location location) {
        String key = entity.getUUID() + MemCacheKey.location;

        if ((! cacheFactory.contains(key)) || (cacheFactory.contains(key) && ! cacheFactory.get(key).equals(location)) ) {

            TaskImpl.startCoreLocationTask(entity, location);
            cacheFactory.put(key, location);
        }
        else {
            log.info("did not report Location since it is already in the cache" + location);
        }


    }

}