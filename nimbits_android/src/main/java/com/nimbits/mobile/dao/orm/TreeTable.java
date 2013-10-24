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

package com.nimbits.mobile.dao.orm;

/**
 * Created by benjamin on 10/23/13.
 */
public class TreeTable extends TableBase {

    public static final String TREE_TABLE_NAME = "TREE";
    public static final String[] fk = {"_fk", "INTEGER"};
    public static final String[] parent = {"PARENT", "TEXT"};
    public static final String[] type = {"TYPE", "INTEGER"};
    public static final String[] state = {"STATE", "INTEGER"};
    public static final String[] value = {"VALUE", "TEXT"};
    public static final String[] timestamp = {"TIMESTAMP", "LONG"};


    public static final String[] entityId = {"ENTITY_ID", "TEXT"};

    public static String getCreateSQL() {
      return
                "CREATE TABLE " + TREE_TABLE_NAME + " (" +
                        id[0] + " " + id[1] + "," +
                        entityId[0] + " " + entityId[1] + "," +
                        fk[0] + " " + fk[1] + "," +
                        name[0] + " " + name[1] + "," +
                        parent[0] + " " + parent[1] + "," +
                        type[0] + " " + type[1] + "," +
                        state[0] + " " + state[1] + "," +
                        value[0] + " " + value[1] + "," +
                        timestamp[0] + " " + timestamp[1] + "," +
                        "FOREIGN KEY(" + fk[0] + ") REFERENCES " + InstanceTable.name[0] + "(" + id[0] + "))"
                ;
    }

    public static String getTreeTableName() {
        return TREE_TABLE_NAME;
    }

    public static String getValue() {
        return value[0];
    }

    public static String getTimestamp() {
        return timestamp[0];
    }

    public static String  getFk() {
        return fk[0];
    }

    public static String getParent() {
        return parent[0];
    }

    public static String getType() {
        return type[0];
    }

    public static String getState() {
        return state[0];
    }

    public static String getEntityId() {
        return entityId[0];
    }
}
