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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.category;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;

import java.util.List;

public interface CategoryServiceAsync {
    void getCategories(final boolean includePoints, final boolean includeDiagrams, final boolean includeAlertState,
                       AsyncCallback<List<Category>> asyncCallback) throws NimbitsException;

    void getConnectionCategories(final boolean includePoints, final boolean includeDiagrams, final EmailAddress email,
                                 AsyncCallback<List<Category>> asyncCallback) throws NimbitsException;

    void addCategory(final CategoryName CategoryName,
                     AsyncCallback<Category> asyncCallback) throws NimbitsException;

    void deleteCategory(final Category c,
                        AsyncCallback<Void> asyncCallback) throws NimbitsException;

    void getCategories(final User u, final boolean includePoints, final boolean includeDiagrams,
                       AsyncCallback<List<Category>> callback);

    void getCategoryByName(final CategoryName categoryName, final boolean includePoints, final boolean includeDiagrams, AsyncCallback<Category> async) throws NimbitsException;

    void getCategory(final User user, final CategoryName categoryName, AsyncCallback<Category> async);

    void createHiddenCategory(User u, AsyncCallback<Category> async);

    void categoryExists(User u, CategoryName categoryName, AsyncCallback<Boolean> async) throws NimbitsException;

    void addCategory(final User u, final CategoryName categoryName, AsyncCallback<Category> async);

    void getCategory(User u, long catID, AsyncCallback<Category> async);

    void deleteCategory(final User u, final Category c, AsyncCallback<Void> async);

    void updateCategory(Category category, AsyncCallback<Category> asyncCallback) throws NimbitsException;

    void publishCategory(Category category, AsyncCallback<Category> asyncCallback) throws NimbitsException;

    void getCategoryByUUID(String uuidParam, AsyncCallback<Category> async);

    void updateCategory(User u, Category category, AsyncCallback<Category> async);
}
