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

package com.nimbits.cloudplatform.client.model.category;

import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/9/12
 * Time: 2:17 PM
 */
public class CategoryModel extends EntityModel implements Serializable, Category {

    protected CategoryModel() {

    }

    public CategoryModel(Entity entity)  {
        super(entity);
    }
}
