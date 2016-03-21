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

package com.nimbits.client.model.entity;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.hal.Embedded;
import com.nimbits.client.model.hal.Links;
import com.nimbits.client.model.user.User;

import java.io.Serializable;
import java.util.List;

public interface Entity extends Serializable, Comparable<Entity> {

    EntityName getName();

    String getUUID();

    void setUUID(String uuid);

    void setName(EntityName name);

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

    void setChildren(List<Entity> children);

    void update(Entity update);

    List<Entity> getChildren();

    boolean isOwner(User user);

    void validate(User user);

    void setEmbedded(Embedded embedded);

    void setLinks(Links links);

    void setKey(String key);

    Action getAction();

    void setAction(Action action);


}
