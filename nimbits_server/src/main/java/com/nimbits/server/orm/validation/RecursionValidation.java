/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.orm.validation;


import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
import com.nimbits.server.orm.TriggerEntity;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class RecursionValidation {
    private static final int MAX_RECURSION = 10;
    private static final int INT = 1024;
    private static final Logger log = Logger.getLogger(RecursionValidation.class.getName());


    public RecursionValidation( ) {

    }

    public void validate(final EntityDao entityDao, final User user, final Trigger entity) {

        if (StringUtils.isEmpty(entity.getTarget())) {
            throw new IllegalArgumentException("Missing target");
        }
        if (StringUtils.isEmpty(entity.getTrigger())) {
            throw new IllegalArgumentException("Missing trigger");
        }

        Optional<Entity> optional = entityDao.getEntity(user, entity.getOwner(), EntityType.user);

        if (optional.isPresent() && entity.isEnabled()) {
            validateAgainstExisting(entityDao, (User) optional.get(), entity);
        }


    }

    private void validateAgainstExisting(final EntityDao entityDao, User user, Trigger entity) {

        Map<String, String> map = new HashMap<String, String>(INT);
        map.put(entity.getTrigger(), entity.getTarget());

        for (EntityType type : EntityType.values()) {

            Class cls;
            try {
                cls = Class.forName(type.getClassName());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }

            if (cls.getSuperclass().equals(TriggerEntity.class)) {
                Map<String, Entity> all = entityDao.getEntityMap(
                        user,
                        type,
                        1000);

                Iterable<Entity> entities = new ArrayList<Entity>(all.values());
                for (Entity e : entities) {

                    Trigger c = (Trigger) e;
                    log.info(c.getTrigger() + ">>>" + c.getTarget());
                    if (!StringUtils.isEmpty(c.getTrigger()) && !StringUtils.isEmpty(c.getTarget())) {
                        map.put(c.getTrigger(), c.getTarget());
                    }
                }
            }
        }

        testRecursion(map, entity);

    }

    private static void testRecursion(Map<String, String> map, Trigger trigger) {

        if (map.containsKey(trigger.getTarget())) {  //then target is a calc
            String currentTrigger = trigger.getTrigger();
            int count = 0;
            while (true) {
                String currentTarget = map.get(currentTrigger);
                if (map.containsKey(currentTarget)) {

                    count++;
                    if (count > MAX_RECURSION) {
                        log.warning("trigger failed validation with recursion test");
                        throw new IllegalArgumentException("The target for this trigger is a trigger for another entity. That's ok, but the" +
                                "target for that calc is also the trigger for another, and so on for over " + MAX_RECURSION + " steps. We " +
                                "stopped checking after that, but it looks like this is an infinite loop." + " enabled=" + trigger.isEnabled());

                    }
                } else {
                    break;
                }
            }

        }

    }

}

