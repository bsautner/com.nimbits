/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.model;

import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.value.Value;

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
}
