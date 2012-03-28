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

import java.io.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/8/11
 * Time: 5:42 PM
 */
@SuppressWarnings("unused")
public class GxtModel extends BaseTreeModel implements Serializable {
    private String uuid;
    private EntityName name;
    private AlertType alertType;
    private EntityType entityType;
    private boolean isReadOnly;
    private boolean isDirty;
    private Value value;
    private Entity baseEntity;


    public GxtModel(Entity entity) {
        setEntityValues(entity);
    }
    public GxtModel(User user) throws NimbitsException {
        this.uuid = user.getUuid();
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

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public EntityType getEntityType() {
        return entityType;
    }


    public String getId() {
        return uuid;
    }

    public void setId(String id) {
        this.uuid = id;
    }

    public EntityName getName() {
        return this.name;
    }

    public void setName(EntityName name) {
        this.name = name;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public boolean isDirty() {

        return isDirty;
    }

    public void setDirty(boolean dirty) {
        set(Parameters.dirty.getText(), dirty ? Parameters.yes.getText() : Parameters.no.getText());
        isDirty = dirty;
    }

    public String getUUID() {
        return uuid;
    }

    public void setValue(Value value) {

        StringBuilder sb = new StringBuilder();
        if ( value.getDoubleValue() != Const.CONST_IGNORED_NUMBER_VALUE) {
            sb.append(value.getDoubleValue());
        }
        if (! Utils.isEmptyString(value.getNote())) {
            sb.append(" ");
            sb.append(value.getNote());
        }
        set(Parameters.value.getText(), sb.toString().trim());
        this.value = value;
    }

    public Entity getBaseEntity() {
        return baseEntity;
    }

    public void update(Entity entity) {
       setEntityValues(entity);
    }

    private void setEntityValues(Entity entity) {
        this.uuid = entity.getEntity();
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
