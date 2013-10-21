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

import com.nimbits.client.model.entity.Entity;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/9/12
 * Time: 7:09 PM
 */
public class CategoryFactory {

    private CategoryFactory() {
    }

    public static Category createCategory(Category e)  {
        return new CategoryModel(e);
    }
    public static Category createCategory(Entity e)  {
        return new CategoryModel(e);
    }
}
