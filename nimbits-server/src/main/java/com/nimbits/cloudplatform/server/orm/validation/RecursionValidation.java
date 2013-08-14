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

package com.nimbits.cloudplatform.server.orm.validation;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.trigger.Trigger;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.orm.TriggerEntity;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.shared.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/19/12
 * Time: 12:02 PM
 */
@Component("recursionValidation")
public class RecursionValidation {
    private static final int MAX_RECURSION = 10;
    private static final int INT = 1024;
    private static final Logger log = Logger.getLogger(RecursionValidation.class.getName());


    public static void validate(final User user, final Trigger entity)  {

        if (Utils.isEmptyString(entity.getTarget())) {
            throw new IllegalArgumentException("Missing target");
        }
        if (Utils.isEmptyString(entity.getTrigger())) {
            throw new IllegalArgumentException("Missing trigger");
        }

        List<Entity> userList = EntityServiceImpl.getEntityByKey(user, entity.getOwner(), EntityType.user);

        if (userList.isEmpty()){
            throw new IllegalArgumentException("Could not locate the owner of this trigger with the key provided!");
        }
        else {
            if (entity.isEnabled())  {
            validateAgainstExisting(userList, entity);
            }
        }


    }
    private static void validateAgainstExisting(List<Entity> user, Trigger entity)  {

        Map<String, String> map = new HashMap<String, String>(INT);
        map.put(entity.getTrigger(), entity.getTarget());

            for (EntityType type : EntityType.values()) {

                Class cls = null;
                try {
                    cls = Class.forName(type.getClassName());
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }

                if (cls.getSuperclass().equals(TriggerEntity.class)) {
                    Map<String, Entity> all = EntityServiceImpl.getEntityModelMap(
                            (User) user.get(0),
                            type,
                            1000);

                    Iterable<Entity> entities = new ArrayList<Entity>(all.values());
                    for (Entity e : entities) {

                        Trigger c = (Trigger)e;
                        log.info(c.getTrigger() + ">>>" + c.getTarget());
                        if (! Utils.isEmptyString(c.getTrigger()) && ! Utils.isEmptyString(c.getTarget())) {
                        map.put(c.getTrigger(), c.getTarget());
                        }
                    }
                }
            }

            testRecursion(map,entity);

    }

    private static void testRecursion(Map<String, String> map,  Trigger trigger)  {

        if (map.containsKey(trigger.getTarget())) {  //then target is a calc
            String currentTrigger = trigger.getTrigger();
            int count = 0;
            while (true) {
                String currentTarget = map.get(currentTrigger);
                if (map.containsKey(currentTarget)) {
                    log.info(count + " " + currentTarget + ">>>>" + map.get(currentTarget));
                    count++;
                    if (count > MAX_RECURSION) {
                        log.warning("trigger failed validation with recursion test");
                        throw new IllegalArgumentException("The target for this trigger is a trigger for another entity. That's ok, but the" +
                                "target for that calc is also the trigger for another, and so on for over " + MAX_RECURSION + " steps. We " +
                                "stopped checking after that, but it looks like this is an infinite loop." + " enabled=" + trigger.isEnabled());

                    }
                }
                else {
                    break;
                }
            }

        }

    }

}

