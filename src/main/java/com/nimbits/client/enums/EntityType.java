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

package com.nimbits.client.enums;

import com.nimbits.client.model.*;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.webhook.WebHook;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum EntityType implements Serializable {

    user(0, "Users", true, User.class),
    topic(1, "Topics", true, Topic.class),
    group(2,  "Groups", false, Group.class),
    subscription(5,  "Subscriptions", false, Subscription.class),
    sync(15, "Synchronizations", false, Sync.class),
    calculation(7,  "Calculations", true, Calculation.class),
    summary(11, "Summaries", true, Summary.class),
    instance(14, "Instances", false, Instance.class),
    schedule(21, "Schedules", false, Schedule.class),
    webhook(22,"Web Hooks", false, WebHook.class),
    event(23,"Events", false, Event.class),
    filter(24, "Filters", false, Filter.class);


 //   event(23, false, Event.class);

    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>(EntityType.values().length);
    private static final Map<String, EntityType> lookupName = new HashMap<String, EntityType>(EntityType.values().length);


    static {
        for (EntityType s : EnumSet.allOf(EntityType.class)) {
            lookup.put(s.code, s);
            lookupName.put(s.name(), s);


        }
    }




    private final int code;
    private final boolean uniqueNameFlag;
    private final String friendlyPluralName;
    private final Class<?> clz;


    EntityType(final int code,
               final String friendlyPluralName,
               final boolean uniqueNameFlag,
               final Class<?> clz) {
        this.code = code;
        this.uniqueNameFlag = uniqueNameFlag;
        this.clz = clz;
        this.friendlyPluralName = friendlyPluralName;


    }


    public int getCode() {
        return code;
    }

    public String getFriendlyPluralName() {
        return friendlyPluralName;
    }

    public static EntityType getName(String name) {
        if (lookupName.containsKey(name)) {
            return lookupName.get(name);
        } else {
            return null;
        }
    }


    public static EntityType get(int code) {
        return lookup.get(code);
    }

    public boolean isUniqueNameFlag() {
        return uniqueNameFlag;
    }

    public Class<?> getClz() {
        return clz;
    }
}
