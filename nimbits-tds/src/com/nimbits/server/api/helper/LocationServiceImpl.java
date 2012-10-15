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
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.location.Location;
import com.nimbits.server.process.task.TaskImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;
@Service("locationService")
@Transactional
public class LocationServiceImpl implements LocationService {
  
    protected final static Logger log = Logger.getLogger(LocationServiceImpl.class.getName());
    private TaskImpl taskFactory;
    private MemcacheService cacheFactory;

    public LocationServiceImpl() {
      
    }

    @Override
    public void reportLocation(final Entity entity,final Location location) {
        String key = entity.getUUID() + MemCacheKey.location;

        if ((! cacheFactory.contains(key)) || (cacheFactory.contains(key) && ! cacheFactory.get(key).equals(location)) ) {

            taskFactory.startCoreLocationTask(entity, location);
            cacheFactory.put(key, location);
        }
        else {
            log.info("did not report Location since it is already in the cache" + location);
        }


    }


    public void setTaskFactory(TaskImpl taskFactory) {
        this.taskFactory = taskFactory;
    }

    public TaskImpl getTaskFactory() {
        return taskFactory;
    }

    public void setCacheFactory(MemcacheService cacheFactory) {
        this.cacheFactory = cacheFactory;
    }
}