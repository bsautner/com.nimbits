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

package com.nimbits.client.model.category;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;

import java.io.Serializable;


public class CategoryModel extends EntityModel implements Serializable, Category {

    protected CategoryModel() {

    }

    public CategoryModel(Entity entity) {
        super(entity);
    }

    public CategoryModel(User user, Entity parent, String categoryName) {

        super(CommonFactory.createName(categoryName, EntityType.category),
                "", EntityType.category, ProtectionLevel.everyone, parent.getKey(), user.getEmail().getValue(), ""
        );

    }

    public CategoryModel(EntityName categoryName) {

        super(categoryName,
                "", EntityType.category, ProtectionLevel.everyone, "", "", ""
        );
    }

    public CategoryModel(Entity parent, String categoryName) {

        super(CommonFactory.createName(categoryName, EntityType.category),
                "", EntityType.category, ProtectionLevel.everyone, parent.getKey(), parent.getOwner(), ""
        );
    }

    public static class Builder extends EntityBuilder {

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, EntityType.category);
            return this;
        }

        public Category create() {


            return new CategoryModel(name);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        @Override
        public Builder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }
    }

    }
