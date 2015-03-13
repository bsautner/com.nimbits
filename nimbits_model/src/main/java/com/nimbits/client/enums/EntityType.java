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

package com.nimbits.client.enums;

import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.sync.Sync;
import com.nimbits.client.model.user.User;


import java.io.Serializable;
import java.util.*;


public enum EntityType implements Serializable {
    user(0, false, false, false, true, true, false, 0, "com.nimbits.server.orm.UserEntity", User.class),
    point(1, true, false, true, true, true, true, 1, "com.nimbits.server.orm.PointEntity", Point.class),
    category(2, true, false, true, false, true, false, 2, "com.nimbits.server.orm.CategoryEntity", Category.class),
    subscription(5, false, false, false, false, true, false, 4, "com.nimbits.server.orm.SubscriptionEntity", Subscription.class),
    sync(15, false, false, false, false, true, false, 5, "com.nimbits.server.orm.SyncEntity", Sync.class),
    calculation(7, false, true, true, false, true, false, 6, "com.nimbits.server.orm.CalcEntity", Calculation.class),
    summary(11, false, true, false, false, true, false, 11, "com.nimbits.server.orm.SummaryEntity", Summary.class),
    accessKey(13, false, false, false, true, true, false, 13, "com.nimbits.server.orm.AccessKeyEntity", AccessKey.class),
    instance(14, true, false, false, true, false, false, 14, "com.nimbits.server.orm.InstanceEntity", Instance.class),
    socket(19, false, false, false, true, true, false, 19, "com.nimbits.server.orm.SocketEntity", Socket.class),
    connection(20, false, false, false, true, true, false, 20, "com.nimbits.server.orm.ConnectionEntity", Connection.class),
    schedule(21, false, false, false, false, true, false, 21, "com.nimbits.server.orm.ScheduleEntity", Schedule.class),
    ;

    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>(EntityType.values().length);

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class)) {
            lookup.put(s.code, s);

        }
    }

    private final Class<?> clz;

    public static List<String> classList() {
        List<String> retObj = new ArrayList<String>();
        for (final EntityType e : EntityType.values()) {
            if (!retObj.contains(e.className)) {
                retObj.add(e.className);
            }
        }
        return retObj;

    }

    private final int code;
    private final boolean uniqueNameFlag;
    private final String className;
    private final boolean isTreeGridItem;
    private final int order;
    private final boolean sendUpdatesToCore;
    private final boolean recordsData;
    private final boolean isTrigger;
    private final boolean isAndroidReady;

    private EntityType(final int code,
                       final boolean isAndroidReady,
                       final boolean isTrigger,
                       final boolean sendUpdatesToCore,
                       final boolean uniqueNameFlag,
                       final boolean isTreeGridItem,
                       final boolean recordsData,
                       final int order,
                       final String className,
                       final Class<?> clz) {
        this.code = code;
        this.isAndroidReady = isAndroidReady;
        this.uniqueNameFlag = uniqueNameFlag;
        this.className = className;
        this.isTreeGridItem = isTreeGridItem;
        this.order = order;
        this.sendUpdatesToCore = sendUpdatesToCore;
        this.recordsData = recordsData;
        this.isTrigger = isTrigger;
        this.clz = clz;

    }

    public boolean isSendUpdatesToCore() {
        return sendUpdatesToCore;
    }

    public int getCode() {
        return code;
    }

    public static EntityType get(int code) {
        return lookup.get(code);
    }

    public boolean isUniqueNameFlag() {
        return uniqueNameFlag;
    }

    public String getClassName() {
        return className;
    }

    public boolean isTreeGridItem() {
        return isTreeGridItem;
    }

    public Integer getOrder() {
        return order;
    }

    public boolean recordsData() {
        return recordsData;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public boolean isAndroidReady() {
        return isAndroidReady;
    }

    public static CharSequence[] toAndroidOptionArray() {

        List<String> l = toList();
        return toList().toArray(new CharSequence[l.size()]);
    }

    public static List<String> toList() {

        List<String> values = new ArrayList<String>(); //don't set size

        for (EntityType s : EnumSet.allOf(EntityType.class)) {
            if (s.isTreeGridItem() && s.isAndroidReady) {
                values.add(s.name());
            }

        }

        return values;
    }

    public static List<EntityType> toTypeList() {

        List<EntityType> values = new ArrayList<EntityType>(); //don't set size

        for (EntityType s : EnumSet.allOf(EntityType.class)) {
            //  if (s.isTreeGridItem() && s.isAndroidReady) {
            values.add(s);
            //  }

        }

        return values;
    }


    public Class<?> getClz() {
        return clz;
    }
}
