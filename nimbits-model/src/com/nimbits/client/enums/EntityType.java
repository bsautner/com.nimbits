package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum EntityType {
    user(0, true, true,  "com.nimbits.server.orm.UserEntity"),
    point(1, true,true, "com.nimbits.server.orm.PointEntity"),
    category(2, false,true,  "com.nimbits.server.orm.CategoryEntity"),
    file(4, false,true, "com.nimbits.server.orm.FileEntity"),
    subscription(5, false,true, "com.nimbits.server.orm.SubscriptionEntity"),
    userConnection(6, false,false, "com.nimbits.server.orm.EntityStore"),
    calculation(7, false,true, "com.nimbits.server.orm.CalcEntity"),
    intelligence(8, false,true, "com.nimbits.server.orm.IntelligenceEntity"),
    feed(9, false,false,  "com.nimbits.server.orm.PointEntity"),
    resource(10, true,true, "com.nimbits.server.orm.XmppResourceEntity"),
    summary(11, false, true, "com.nimbits.server.orm.SummaryEntity"),
    instance(12, false, false, "com.nimbits.server.orm.EntityStore");
    private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>(EntityType.values().length);

    static {
        for (EntityType s : EnumSet.allOf(EntityType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final boolean uniqueNameFlag;
    private final String className;
    private final boolean isTreeGridItem;

    private EntityType(int code, boolean uniqueNameFlag, boolean isTreeGridItem, String className) {
        this.code = code;
        this.uniqueNameFlag = uniqueNameFlag;
        this.className = className;
        this.isTreeGridItem = isTreeGridItem;
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
}
