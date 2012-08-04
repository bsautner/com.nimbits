package com.nimbits.client.model;

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
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

    void update(Entity entity) throws NimbitsException;
}
