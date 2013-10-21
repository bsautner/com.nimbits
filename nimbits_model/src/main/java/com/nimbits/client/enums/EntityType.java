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

import java.io.Serializable;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum EntityType implements Serializable {
    user(0, false, false, false, true, true, false, 0, "com.nimbits.server.orm.UserEntity"),
    point(1, true, false, true, true, true, true, 1, "com.nimbits.server.orm.PointEntity"),
    category(2, true, false, true, false, true, false, 2, "com.nimbits.server.orm.CategoryEntity"),
    subscription(5, false, false, false, false, true, false, 4, "com.nimbits.server.orm.SubscriptionEntity"),
    calculation(7, false, true, true, false, true, false, 6, "com.nimbits.server.orm.CalcEntity"),
    summary(11, false, true, false, false, true, false, 11, "com.nimbits.server.orm.SummaryEntity"),
    accessKey(13, false, false, false, true, true, false, 13, "com.nimbits.server.orm.AccessKeyEntity"),
    server(14, true, false, false, true, false, false, 14, "com.nimbits.server.orm.ServerEntity");
    static final long serialVersionUID = 42L;

    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>(EntityType.values().length);

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class))
            lookup.put(s.code, s);
    }

    public static List<String> classList() {
        List<String> retObj = new ArrayList<String>(12);
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
                       final String className) {
        this.code = code;
        this.isAndroidReady = isAndroidReady;
        this.uniqueNameFlag = uniqueNameFlag;
        this.className = className;
        this.isTreeGridItem = isTreeGridItem;
        this.order = order;
        this.sendUpdatesToCore = sendUpdatesToCore;
        this.recordsData = recordsData;
        this.isTrigger = isTrigger;
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
}
