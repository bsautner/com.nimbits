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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/8/11
 * Time: 5:35 PM
 */
public class GxtPointModel extends BaseModelData {
    private final long id;
    private PointName name;
    private final boolean readOnly;
    private boolean isDirty;
    private String UUID;
    private EntityType entityType;
    public void setAlertState(AlertType alertState) {
        this.alertState = alertState;
    }

    private AlertType alertState;

    public GxtPointModel(Point point, ClientType clientType) {
        this.UUID = point.getUUID();
        this.id = point.getId();
        //String lastNote = point.getLastNote();
        this.name = point.getName();
        this.readOnly = point.getReadOnly();
        this.alertState = point.getAlertState();
        this.entityType = point.getEntityType();
        set(Const.PARAM_ID, this.id);
        if (clientType.equals(ClientType.android)) {
            set(Const.PARAM_NAME, "<A href = \"report.html?client=" + clientType.name() + "&uuid=" + point.getUUID() + "\">" + this.name.getValue() + "</a>");
        } else {
            set(Const.PARAM_NAME, this.name.getValue());
        }
        set(Const.PARAM_ICON, Const.PARAM_POINT);
        set(Const.PARAM_VALUE, 0.0);
        set(Const.PARAM_ENTITY_TYPE, point.getEntityType().getCode());
        set(Const.PARAM_UUID, point.getUUID());
    }

    public long getId() {
        return id;
    }

    public PointName getName() {
        return name;
    }

    public void setName(PointName name) {
        this.name = name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(final boolean dirty) {
        isDirty = dirty;
    }

    public AlertType getAlertState() {
        if (alertState == null) {
            alertState = AlertType.OK;
        }
        return alertState;
    }

    public void setValue(Value value) {
        if (value != null) {
           // this.value = value;
            set(Const.PARAM_VALUE, value.getNumberValue());
        }

    }

    public String getUUID() {
        return UUID;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
