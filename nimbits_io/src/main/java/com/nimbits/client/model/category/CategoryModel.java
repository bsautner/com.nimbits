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

package com.nimbits.client.model.category;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;


public class CategoryModel extends EntityModel implements Serializable, Category {

    @SuppressWarnings("unused")
    protected CategoryModel() {

    }


    protected CategoryModel(String id, CommonIdentifier name, String description, EntityType entityType, String parent, String owner) {
        super(id, name, description, entityType, parent, owner);
    }

    public static class Builder extends EntityBuilder {

        private final EntityType type = EntityType.category;

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        public Category create() {


            return new CategoryModel(id, name, description, type, parent, owner);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Category category) {
            super.init(category);
            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder alertType(int alertType) {
            this.alertType = alertType;
            return this;
        }

        @Override
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        @Override
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }


        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }


    }

}
