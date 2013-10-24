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

package com.nimbits.mobile.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.Server;
import com.nimbits.client.model.ServerModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.mobile.application.SessionSingleton;
import com.nimbits.mobile.dao.orm.InstanceTable;
import com.nimbits.mobile.dao.orm.TreeTable;

import java.util.Date;
import java.util.List;


public class ApplicationDaoImpl implements ApplicationDao {

    private static String TAG = "DAO";
    private final SQLiteDatabase db = SessionSingleton.getInstance().getDb();

    public ApplicationDaoImpl() {
 
    }

    @Override
    public Server getServer() {

        Cursor cursor = db.query(InstanceTable.getInstancesTableName(), new String[]
                {InstanceTable.getUrl(), InstanceTable.getIsDefault(), InstanceTable.getId(), InstanceTable.getApikey()},
                InstanceTable.getIsDefault() + "=?",
                new String[]{"1"}, null, null, null, null);


        cursor.moveToFirst();
        return new ServerModel("http://" +
                cursor.getString(cursor.getColumnIndex(InstanceTable.getUrl())),
                cursor.getLong(cursor.getColumnIndex(InstanceTable.getId())),
                cursor.getString(cursor.getColumnIndex(InstanceTable.getApikey())));
    }

    @Override
    public int getCount(long id) {
        Cursor cursor = null;
        try {
           cursor =  db.query(TreeTable.TREE_TABLE_NAME,
                    new String[]
                            {
                                    TreeTable.getId(),

                            },
                    TreeTable.fk[0] + "=?", new String[]{String.valueOf(id),
                    }, null, null, null, null);
            int r =  cursor.getCount();
            return r;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

    }
    @Override
    public void setDefaultInstanceUrl(long id) {
        ContentValues values = new ContentValues();
        values.put(InstanceTable.getIsDefault(), 0);

        ContentValues update = new ContentValues();
        update.put(InstanceTable.getIsDefault(), 1);
        db.update(InstanceTable.INSTANCES_TABLE_NAME, values, InstanceTable.getIsDefault() + " = ?", new String[]{"1"});
        db.update(InstanceTable.INSTANCES_TABLE_NAME, update, InstanceTable.getId() + " = ?", new String[]{String.valueOf(id)});
        SessionSingleton.getInstance().setServer();
    }


    @Override
    public int storeTree(long id, List<Entity> entities, boolean refresh) {

        Log.v(TAG, "Stored " + entities.size());
        int r = 0;
        if (db != null) {

            if (refresh) {
                db.delete(TreeTable.getTreeTableName(), TreeTable.getFk() + " = ?", new String[]{String.valueOf(id)});
            }
            for (Entity entity : entities) {
                if (entity.getEntityType().isAndroidReady() && ! entity.getEntityType().equals(EntityType.user)) {
                    r++;
                    ContentValues values = new ContentValues();
                    values.put(TreeTable.getName(), entity.getName().getValue());
                    values.put(TreeTable.getEntityId(), entity.getKey());
                    values.put(TreeTable.getParent(), entity.getParent());
                    values.put(TreeTable.getType(), entity.getEntityType().getCode());
                    values.put(TreeTable.getState(), 0);
                    values.put(TreeTable.getFk(), id);

                    db.insert(TreeTable.getTreeTableName(), null, values);
                }
            }

        }
        return r;

    }

    @Override
    public Cursor getChildren(Entity entity) {
        long id = SessionSingleton.getInstance().getServer().getId();

        return db.query(TreeTable.TREE_TABLE_NAME,
                new String[]
                        {
                                TreeTable.getId(),
                                TreeTable.getName(),
                                TreeTable.getState(),
                                TreeTable.getType(),
                                TreeTable.getValue(),
                                TreeTable.getTimestamp()
                        },
                TreeTable.fk[0] + "=? AND " + TreeTable.parent[0] + "=?", new String[]{String.valueOf(id),
                entity.getKey()}, null, null, "TYPE DESC, NAME", null);
    }

    @Override
    public Entity getEntity(long id) {
        Log.v(TAG, "getEntity " + id);
        Cursor row = null;
        try {
            row = db.query(TreeTable.getTreeTableName(),
                    new String[]{TreeTable.getName(), TreeTable.getEntityId(), TreeTable.getType(), TreeTable.getEntityId(), TreeTable.getParent()
                    , TreeTable.getValue(), TreeTable.getTimestamp(), TreeTable.getState()},
                    TreeTable.getId() + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (row.moveToFirst()) {
                String entityId = row.getString(row.getColumnIndex(TreeTable.getEntityId()));
                String n = row.getString(row.getColumnIndex(TreeTable.getName()));
                String parent = row.getString(row.getColumnIndex(TreeTable.getParent()));
                int type = row.getInt(row.getColumnIndex(TreeTable.getType()));
                EntityType entityType = EntityType.get(type);
                EntityName name = CommonFactory.createName(n, entityType);
                Entity e =  EntityModelFactory.createEntity(name, entityType);
                e.setKey(entityId);
                e.setParent(parent);
                return e;
            }
            else {
                return SessionSingleton.getInstance().getSession();

            }
        } finally {
            if (row != null) {
                row.close();
            }
        }


    }
    @Override
    public Entity getParent(long id) {
        Cursor row = null;
        Entity e = getEntity(id);
        try {
            row = db.query(TreeTable.getTreeTableName(),
                    new String[]{TreeTable.getId()},
                    TreeTable.getParent() + " = ?",
                    new String[]{e.getParent()}, null, null, null, null);
            if (row.moveToFirst()) {
                long parentId = row.getLong(row.getColumnIndex(TreeTable.getId()));
                return getEntity(parentId);

            }
            else {
                return SessionSingleton.getInstance().getSession();
            }
        } finally {
            if (row != null) {
                row.close();
            }
        }


    }

    @Override
    public long getIdByName(String name) {
        Cursor row = null;
        try {
            row = db.query(TreeTable.getTreeTableName(),
                    new String[]{TreeTable.getId()},
                    TreeTable.getName() + " = ?",
                    new String[]{name}, null, null, null, null);
            if (row.moveToFirst()) {
                long id = row.getLong(row.getColumnIndex(TreeTable.getId()));
                return id;

            }
            else {
                return 0;
            }
        } finally {
            if (row != null) {
                row.close();
            }
        }

    }

    @Override
    public long getParentId(long id) {
        Entity e = getEntity(id);
        Cursor row = null;
        try {
            row = db.query(TreeTable.getTreeTableName(),
                    new String[]{TreeTable.getId()},
                    TreeTable.getEntityId() + " = ?",
                    new String[]{e.getParent()}, null, null, null, null);
            if (row.moveToFirst()) {
                return row.getLong(row.getColumnIndex(TreeTable.getId()));

            }
            else {
                id = getIdByName(SessionSingleton.getInstance().getEmail());
                return id;
            }
        } finally {

            if (row != null) {
                row.close();
            }
        }

    }

    @Override
    public void updateValue(long id, Value value) {
        ContentValues values = new ContentValues();
        values.put(TreeTable.getState(), value.getAlertState().getCode());
        values.put(TreeTable.getValue(), value.getValueWithNote());
        values.put(TreeTable.getTimestamp(), value.getTimestamp().getTime());


        db.update(TreeTable.getTreeTableName(), values, TreeTable.getId() + " = ?", new String[]{String.valueOf(id)});

    }

    @Override
    public Value getValue(long currentEntity) {
        Cursor row = null;
        try {
            row = db.query(TreeTable.getTreeTableName(),
                    new String[]{TreeTable.getValue(), TreeTable.getTimestamp(), TreeTable.getState()},
                    TreeTable.getId() + " = ?",
                    new String[]{String.valueOf(currentEntity)}, null, null, null, null);
            if (row.moveToFirst()) {
                String value = row.getString(row.getColumnIndex(TreeTable.getValue()));
                long timestamp = row.getLong(row.getColumnIndex(TreeTable.getTimestamp()));
                int state = row.getInt(row.getColumnIndex(TreeTable.getState()));
                double vx = value == null ? 0.0 : Double.valueOf(value);

                Value v = ValueFactory.createValueModel(vx, new Date(timestamp), AlertType.get(state));
                return v;
            }
            else {
                return ValueFactory.createValueModel(0.0);

            }
        } finally {
            if (row != null) {
                row.close();
            }
        }
    }


}
