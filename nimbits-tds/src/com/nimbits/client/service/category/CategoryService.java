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

package com.nimbits.client.service.category;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;

import java.util.List;

@RemoteServiceRelativePath(Const.PARAM_CATEGORY)
public interface CategoryService extends RemoteService {
    List<Category> getCategories(final boolean includePoints,
                                 final boolean includeDiagrams,
                                 final boolean includeSubscriptions,
                                 final boolean includeAlertState) throws NimbitsException;

    List<Category> getCategories(final User u,
                                 final boolean includePoints,
                                 final boolean includeDiagrams,
                                 final boolean includeSubscriptions);

    Category addCategory(final CategoryName CategoryName) throws NimbitsException;

    Category addCategory(final User u, final CategoryName categoryName);

    Category getCategoryByName(final CategoryName categoryName,
                               final boolean includePoints,
                               final boolean includeDiagrams
                              ) throws NimbitsException;

    Category getCategoryByName(final User u, final CategoryName categoryName, final boolean includePoints, final boolean includeDiagrams) throws NimbitsException;

    void deleteCategory(final Category c) throws NimbitsException;

    void deleteCategory(final User u, final Category c) throws NimbitsException;

    List<Category> getConnectionCategories(final boolean includePoints,
                                           final boolean includeDiagrams,
                                           final boolean includeSubscriptions,
                                           final EmailAddress email
                                           ) throws NimbitsException;

    Category getCategory(final User user, final CategoryName categoryName);

    Category createHiddenCategory(User u);

    boolean categoryExists(User u, CategoryName categoryName) throws NimbitsException;

    Category getCategory(User u, long catID);

    Category updateCategory(Category category) throws NimbitsException;

    Category updateCategory(User u, Category category);

    Category publishCategory(Category category) throws NimbitsException;

    Category getCategoryByUUID(String uuidParam);


}
