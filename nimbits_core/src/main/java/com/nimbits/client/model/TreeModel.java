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

package com.nimbits.client.model;

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.value.Value;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/10/12
 * Time: 3:53 PM
 */
public interface TreeModel extends com.extjs.gxt.ui.client.data.TreeModel, Serializable {
    AlertType getAlertType();

    void setAlertType(AlertType alertType);

    EntityType getEntityType();

    String getId();

    void setId(String id);

    EntityName getName();

    void setName(EntityName name);

    boolean isReadOnly();

    void setReadOnly(boolean readOnly);

    boolean isDirty();

    void setDirty(boolean dirty);

    String getKey();

    void setValue(Value value);

    Entity getBaseEntity();

    void update(Entity entity) ;

    String getUuid();
}
