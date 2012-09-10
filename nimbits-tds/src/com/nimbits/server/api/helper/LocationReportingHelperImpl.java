/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.api.helper;


import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.location.Location;
import com.nimbits.server.process.task.TaskFactory;

import java.util.logging.Logger;

public class LocationReportingHelperImpl implements LocationReportingHelper {
    private final MemcacheService cache;
    protected final static Logger log = Logger.getLogger(LocationReportingHelperImpl.class.getName());

    public LocationReportingHelperImpl() {
        cache = MemcacheServiceFactory.getMemcacheService(MemCacheKey.defaultNamespace.name());
    }

    @Override
    public void reportLocation(final Entity entity,final Location location) {
        String key = entity.getUUID() + MemCacheKey.location;

        if ((! cache.contains(key)) || (cache.contains(key) && ! cache.get(key).equals(location)) ) {

            TaskFactory.getInstance().startCoreLocationTask(entity, location);
            cache.put(key, location);
        }
        else {
            log.info("did not report Location since it is already in the cache" + location);
        }


    }




}
