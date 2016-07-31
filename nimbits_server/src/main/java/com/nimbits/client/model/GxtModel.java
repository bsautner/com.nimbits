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

package com.nimbits.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;


@SuppressWarnings("unused")
public class GxtModel extends BaseTreeModel implements TreeModel {
    private static final int INT = 40;
    private String id;
    private EntityName name;
    private AlertType alertType;
    private EntityType entityType;
    private boolean isReadOnly;
    private boolean isDirty;
    private Entity baseEntity;


    protected GxtModel() {
    }

    public GxtModel(final Entity entity) {
        setEntityValues(entity);
    }

    public GxtModel(final User user) {
        this.id = user.getId();
        this.name = CommonFactory.createName(user.getEmail().getValue(), EntityType.user);
        this.alertType = AlertType.OK;
        this.entityType = EntityType.user;
        this.isReadOnly = true;
        this.baseEntity =  user;
        set(Parameters.id.getText(), this.id);
        set(Parameters.name.getText(), this.name.getValue());
        set(Parameters.entityType.getText(), this.entityType.getCode());
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
    public void setId(String id) {
        this.id = id;
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
    public String getId() {
        return id;
    }

    @Override
    public void setValue(Value value) {

        StringBuilder sb = new StringBuilder(INT);
        if (value != null && value.getDoubleValue() != null && value.getDoubleValue()  != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(value.getDoubleValue());
        }
        if (value != null && !Utils.isEmptyString(value.getData())) {
            sb.append(' ');
            sb.append(value.getData());
        }
        set(Parameters.value.getText(), sb.toString().trim());
        set(Parameters.data.getText(), value.getData());
        set(Parameters.timestamp.getText(), value.getTimestamp());
    }

    @Override
    public Entity getBaseEntity() {
        return baseEntity;
    }

    @Override
    public void update(Entity entity) {
        setEntityValues(entity);
    }


    private void setEntityValues(Entity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.alertType = entity.getAlertType();
        this.entityType = entity.getEntityType();
        this.isReadOnly = entity.isReadOnly();


        this.baseEntity = entity;
        set(Parameters.id.getText(), this.id);
        set(Parameters.name.getText(), this.name.getValue());
        set(Parameters.entityType.getText(), entity.getEntityType().getCode());

        if (entity.getEntityType().equals(EntityType.point)) {
            set(Parameters.value.getText(), "");
        }


    }
}
