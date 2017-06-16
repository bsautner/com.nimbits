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

import javax.jdo.annotations.PersistenceCapable;
import java.io.Serializable;

//TODO - allow linking to points for views with viewtypes to show grids etc

@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Group extends Entity implements Serializable {

    private final static EntityType entityType = EntityType.group;

    public Group() {
        super(entityType);
    }


    protected Group(String name, String description, String parent, String owner) {
        super(name, description, entityType, parent, owner);
    }

    public static class Builder extends EntityBuilder {



        public Group create() {


            return new Group(name, description, parent, owner);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Group group) {
            super.init(group);
            return this;
        }

        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }


    }

}
