package com.nimbits.client.enums;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum EntityType implements Serializable {
    user(0, false, false, true, true, false,0, "com.nimbits.server.orm.UserEntity"),
    point(1, false, true, true,true, true, 1, "com.nimbits.server.orm.PointEntity"),
    category(2, false,true, false,true,false, 2, "com.nimbits.server.orm.CategoryEntity"),
    subscription(5, false,false, false,true,false, 4, "com.nimbits.server.orm.SubscriptionEntity"),
    userConnection(6, false,false, true,true,false, 5, "com.nimbits.server.orm.ConnectionEntity"),
    calculation(7, true, true, false,true,false, 6, "com.nimbits.server.orm.CalcEntity"),
    intelligence(8, true, true, false,true,false, 7, "com.nimbits.server.orm.IntelligenceEntity"),
    feed(9, false, false,true,false, true, 8, "com.nimbits.server.orm.PointEntity"),
    resource(10, false,false, true,true, false, 10, "com.nimbits.server.orm.XmppResourceEntity"),
    summary(11, true, false, false, true,false, 11,"com.nimbits.server.orm.SummaryEntity"),
    instance(12, false,true, false, false,false, 12, "com.nimbits.server.orm.CategoryEntity"),
    accessKey(13, false, false, true, true,false, 13, "com.nimbits.server.orm.AccessKeyEntity");

    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>(EntityType.values().length);

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final boolean uniqueNameFlag;
    private final String className;
    private final boolean isTreeGridItem;
    private final int order;
    private final boolean sendUpdatesToCore;
    private final boolean recordsData;
    private final boolean isTrigger;
    private EntityType(final int code,
                       final boolean isTrigger,
                       final boolean sendUpdatesToCore,
                       final boolean uniqueNameFlag,
                       final boolean isTreeGridItem,
                       final boolean recordsData,
                       final int order,
                       final String className) {
        this.code = code;
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
}
