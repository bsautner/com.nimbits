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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.subscription.EventType;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@PersistenceCapable
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Subscription extends Entity implements Serializable {

    private final static EntityType entityType = EntityType.subscription;

    @Persistent
    private String subscribedEntity;

    @Persistent
    private int notifyMethod;

    @Persistent
    private int eventType;

    @Persistent
    private String target;

    @Persistent
    private boolean enabled;


    public Subscription() {
        super(entityType);
    }


    public Subscription(String name, String description, String parent, String owner, String subscribedEntity, int notifyMethod, int eventType, String target, boolean enabled) {
        super(name, description, entityType, parent, owner);
        this.subscribedEntity = subscribedEntity;
        this.notifyMethod = notifyMethod;
        this.eventType = eventType;
        this.target = target;
        this.enabled = enabled;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Subscription update = (Subscription) entity;
        this.subscribedEntity = update.getSubscribedEntity();
        this.notifyMethod = update.getNotifyMethod().getCode();
        this.eventType = update.getEventType().getCode();
        this.target = update.getTarget();
        this.enabled = update.getEnabled();
    }


    String getSubscribedEntity() {
        return this.subscribedEntity;
    }


    public boolean getEnabled() {
        return this.enabled;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public SubscriptionNotifyMethod getNotifyMethod() {
        return SubscriptionNotifyMethod.get(this.notifyMethod);
    }


    public void setNotifyMethod(SubscriptionNotifyMethod notifyMethod) {
        this.notifyMethod = notifyMethod.getCode();
    }


    public EventType getEventType() {
        return EventType.get(eventType);
    }


    public void setEventType(EventType eventType) {
        this.eventType = eventType.getCode();
    }


    public String getTarget() {
        return target == null ? "" : target;
    }


    public void setTarget(String target) {
        this.target = target;
    }


    public static class Builder extends EntityBuilder {

        private final EntityType type = EntityType.subscription;

        private String subscribedEntity;

        private SubscriptionNotifyMethod notifyMethod;

        private EventType eventType;

        private String target;

        private boolean enabled;

        public Builder subscribedEntity(String subscribedEntity) {
            this.subscribedEntity = subscribedEntity;
            return this;
        }

        public Builder subscribedEntity(Entity subscribedEntity) {
            this.subscribedEntity = subscribedEntity.getId();
            return this;
        }

        public Builder notifyMethod(SubscriptionNotifyMethod notifyMethod) {
            this.notifyMethod = notifyMethod;
            return this;
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder target(String target) {
            this.target = target;
            return this;
        }

        public Builder target(Entity target) {
            this.target = target.getId();
            if (target.getEntityType().equals(EntityType.webhook)) {
                this.notifyMethod(SubscriptionNotifyMethod.webhook);
            }
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }


        public Subscription create() {
            if (eventType == null) {
                eventType = EventType.none;
            }
            if (notifyMethod == null) {
                notifyMethod = SubscriptionNotifyMethod.none;
            }
            return new Subscription(name, description, parent
                    , owner, subscribedEntity, notifyMethod.getCode(), eventType.getCode(), target, enabled);

        }


        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder description(String description) {
            this.description = description;
            return this;
        }


        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }


        public Builder init(Subscription entity) {
            super.init(entity);
            this.subscribedEntity = entity.getSubscribedEntity();
            this.notifyMethod = entity.getNotifyMethod();
            this.eventType = entity.getEventType();
            this.target = entity.getTarget();
            this.enabled = entity.getEnabled();
            return this;
        }


    }
}
