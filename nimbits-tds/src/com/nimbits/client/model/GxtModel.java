/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model;

import com.extjs.gxt.ui.client.data.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/8/11
 * Time: 5:42 PM
 */
@SuppressWarnings("unused")
public class GxtModel extends BaseTreeModel implements TreeModel {
    private static final int INT = 40;
    private String uuid;
    private EntityName name;
    private AlertType alertType;
    private EntityType entityType;
    private boolean isReadOnly;
    private boolean isDirty;
    private Value value;
    private Entity baseEntity;


    public GxtModel(Entity entity) throws NimbitsException {
        setEntityValues(entity);
    }
    public GxtModel(User user) throws NimbitsException {
        this.uuid = user.getKey();
        this.name = CommonFactoryLocator.getInstance().createName(user.getEmail().getValue(), EntityType.user);
        this.alertType = AlertType.OK;
        this.entityType = EntityType.user;
        this.isReadOnly = true;
        this.baseEntity = EntityModelFactory.createEntity(user);
        set(Parameters.id.getText(), this.uuid);
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
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
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
    public String getUUID() {
        return uuid;
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
        this.value = value;
    }

    @Override
    public Entity getBaseEntity() {
        return baseEntity;
    }

    @Override
    public void update(Entity entity) throws NimbitsException {
       setEntityValues(entity);
    }

    private void setEntityValues(Entity entity) throws NimbitsException {
        this.uuid = entity.getKey();
        this.name = entity.getName();
        this.alertType = entity.getAlertType();
        this.entityType = entity.getEntityType();
        this.isReadOnly = entity.isReadOnly();
        this.baseEntity = entity;
        set(Parameters.id.getText(), this.uuid);
        set(Parameters.name.getText(), this.name.getValue());
        set(Parameters.entityType.getText(), entity.getEntityType().getCode());
    }
}
