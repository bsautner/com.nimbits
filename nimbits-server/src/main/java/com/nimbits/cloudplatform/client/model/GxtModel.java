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

package com.nimbits.cloudplatform.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/8/11
 * Time: 5:42 PM
 */
@SuppressWarnings("unused")
public class GxtModel extends BaseTreeModel implements TreeModel {
    private static final int INT = 40;
    private String key;
    private EntityName name;
    private AlertType alertType;
    private EntityType entityType;
    private boolean isReadOnly;
    private boolean isDirty;
    private Entity baseEntity;
    private String uuid;
    protected GxtModel(){}

    public GxtModel(final Entity entity)  {
        setEntityValues(entity);
    }
    public GxtModel(final User user)  {
        this.key = user.getKey();
        this.name = CommonFactory.createName(user.getEmail().getValue(), EntityType.user);
        this.alertType = AlertType.OK;
        this.entityType = EntityType.user;
        this.isReadOnly = true;
        this.baseEntity = EntityModelFactory.createEntity(user);
        set(Parameters.id.getText(), this.key);
        set(Parameters.name.getText(), this.name.getValue());
        set(Parameters.entityType.getText(),  this.entityType.getCode());
        set(Parameters.dirty.getText(), Parameters.no.getText());

    }

    @Override
    public AlertType getAlertType() {
        return alertType;
    }

    @Override
    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }


    @Override
    public String getId() {
        return key;
    }

    @Override
    public void setId(String id) {
        this.key = id;
    }

    @Override
    public EntityName getName() {
        return this.name;
    }

    @Override
    public void setName(EntityName name) {
        this.name = name;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    @Override
    public boolean isDirty() {

        return isDirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        set(Parameters.dirty.getText(), dirty ? Parameters.yes.getText() : Parameters.no.getText());
        isDirty = dirty;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setValue(Value value) {

        StringBuilder sb = new StringBuilder(INT);
        if ( value.getDoubleValue() != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(value.getDoubleValue());
        }
        if (! Utils.isEmptyString(value.getNote())) {
            sb.append(' ');
            sb.append(value.getNote());
        }
        set(Parameters.value.getText(), sb.toString().trim());
        set(Parameters.data.getText(), value.getData().getContent());
        set(Parameters.timestamp.getText(), value.getTimestamp());
    }

    @Override
    public Entity getBaseEntity() {
        return baseEntity;
    }

    @Override
    public void update(Entity entity)  {
       setEntityValues(entity);
    }
    @Override
    public String getUuid() {
        return uuid;
    }

    private void setEntityValues(Entity entity)  {
        this.key = entity.getKey();
        this.name = entity.getName();
        this.alertType = entity.getAlertType();
        this.entityType = entity.getEntityType();
        this.isReadOnly = entity.isReadOnly();
        this.uuid = entity.getUUID();


        this.baseEntity = entity;
        set(Parameters.id.getText(), this.key);
        set(Parameters.name.getText(), this.name.getValue());
        set(Parameters.entityType.getText(), entity.getEntityType().getCode());

        if (entity.getEntityType().equals(EntityType.point)) {
            set(Parameters.value.getText(), "");
        }


    }
}
