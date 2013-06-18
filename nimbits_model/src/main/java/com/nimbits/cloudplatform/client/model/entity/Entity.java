package com.nimbits.cloudplatform.client.model.entity;

import com.nimbits.cloudplatform.client.enums.AlertType;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 10:50 AM
 */
public interface Entity  extends Serializable, Comparable<Entity> {
    EntityName getName();

    String getUUID();

    void setUUID(String uuid);

    void setName(EntityName name) ;

    String getDescription();

    void setDescription(String description);

    EntityType getEntityType();

    void setEntityType(EntityType entityType);

    String getKey();

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

    void setChildren(List<Point> children);

    void update(Entity update) ;

    List<Point> getChildren();

    boolean isOwner(User user);

    boolean entityIsReadable(User user) ;

    Date getDateCreated();

    void validate(User user) ;

    void setDateCreated(Date dateCreated);

    void setKey(String key) ;

    String getInstanceUrl();

    boolean isCached();

    void setIsCached(boolean isCached) ;


}
