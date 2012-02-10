/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.pointcategory;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.entity.EntityName;

import java.util.List;

public interface CategoryTransactions {

    void purgeMemCache();

    Category getCategory(final long id);

    Category createHiddenCategory();

    @Deprecated
    List<Category> getCategories(final boolean includePoints, final boolean includeDiagrams, final boolean includeSubscriptions);

    public Category getCategory(final EntityName categoryName);

    boolean categoryExists(final EntityName EntityName) throws NimbitsException;

    //  Category addCategory(final EntityName EntityName, final User nimbitsUser);
    Category addCategory(final EntityName categoryName);

    void deleteCategory(final Category c);

    Category updateCategory(Category category);

    Category getCategoryByUUID(String uuidParam);
}