package com.nimbits.client.model.entity;

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.point.*;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:50 AM
 */
public interface Entity  extends Serializable {
    EntityName getName();

    void setName(EntityName name);

    String getDescription();

    void setDescription(String description);

    EntityType getEntityType();

    void setEntityType(EntityType entityType);

    String getEntity();

    void setEntity(String entity);

    String getParent();

    void setParent(String parent);

    ProtectionLevel getProtectionLevel();

    void setProtectionLevel(ProtectionLevel protectionLevel);

    String getOwner();

    void setOwner(String owner);

    AlertType getAlertType();

    void setAlertType(AlertType alertType);

    boolean isReadOnly();

    void setReadOnly(boolean readOnly);

    @Deprecated
    String getUUID();

    @Deprecated
    void setUUID(String newUUID);

    String getBlobKey();

    void setBlobKey(String blobKey);

    List<Entity> getChildren();

    void addChild(Entity entity);


    void setPoints(List<Point> points);

    void setHost(String host);
}
