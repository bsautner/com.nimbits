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

package com.nimbits.server.memcache.category;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.memcache.MemCacheHelper;
import com.nimbits.server.pointcategory.CategoryTransactionFactory;
import com.nimbits.server.pointcategory.CategoryTransactions;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/22/11
 * Time: 12:15 PM
 */
public class CategoryMemCacheImpl implements CategoryTransactions {

    private final MemcacheService cache;
    private final User user;

    public CategoryMemCacheImpl(final User u) {
        user = u;
        if (u != null) {
            cache = MemcacheServiceFactory.getMemcacheService(Const.CONST_SERVER_VERSION + u.getUuid());
        } else {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheHelper.DEFAULT_CACHE_NAMESPACE);
        }

    }


    @Override
    public void purgeMemCache() {
        if (cache.contains(MemCacheHelper.categoryCollection(user))) {
            cache.delete(MemCacheHelper.categoryCollection(user));
        }
    }

    @Override
    public Category getCategory(final long id) {

        return CategoryTransactionFactory.getDaoInstance(user).getCategory(id);
    }

    @Override
    public Category createHiddenCategory() {
        final Category c = CategoryTransactionFactory.getDaoInstance(user).createHiddenCategory();
        cache.put(c.getName(), c);
        purgeMemCache();
        return c;
    }

    @Override
    public List<Category> getCategories(final boolean includePoints,
                                        final boolean includeDiagrams,
                                        final boolean includeSubscriptions) {
        List<Category> retObj;
        try {
            if (cache.contains(MemCacheHelper.categoryCollection(user))) {
                retObj = (List<Category>) cache.get(MemCacheHelper.categoryCollection(user));

            } else {
                List<Category> store = CategoryTransactionFactory.getDaoInstance(user).getCategories(includePoints, includeDiagrams, includeSubscriptions);
                //only store complete trees in the cache.
                if (includeDiagrams && includePoints && includeSubscriptions) {
                    cache.put(MemCacheHelper.categoryCollection(user), store);

                }
                retObj = store;
            }

        } catch (InvalidValueException e) {
            cache.delete(cache.get(MemCacheHelper.categoryCollection(user)));
            List<Category> store = CategoryTransactionFactory.getDaoInstance(user).getCategories(includePoints, includeDiagrams, includeSubscriptions);
            cache.put(MemCacheHelper.categoryCollection(user), store);
            retObj = store;
        }


        return retObj;

    }

    @Override
    public Category getCategory(final CategoryName categoryName) {
        if (cache.contains(categoryName)) {
            return (Category) cache.get(categoryName);
        } else {
            Category c = CategoryTransactionFactory.getDaoInstance(user).getCategory(categoryName);
            if (c != null) {
                cache.put(categoryName, c);
            }
            return c;
        }

    }

    @Override
    public boolean categoryExists(final CategoryName categoryName) {
        return (getCategory(categoryName) != null);
    }

    @Override
    public Category addCategory(final CategoryName categoryName) {
        Category c = CategoryTransactionFactory.getDaoInstance(user).addCategory(categoryName);
        cache.put(categoryName, c);
        purgeMemCache();
        return c;
    }

    @Override
    public void deleteCategory(final Category c) {
        if (cache.contains(c.getName())) {
            cache.delete(c);
        }
        purgeMemCache();
        CategoryTransactionFactory.getDaoInstance(user).deleteCategory(c);
    }

    @Override
    public Category updateCategory(Category category) {
        if (cache.contains(category.getName())) {
            cache.delete(category);
        }
        purgeMemCache();
        return CategoryTransactionFactory.getDaoInstance(user).updateCategory(category);
    }

    @Override
    public Category getCategoryByUUID(String uuidParam) {
        return CategoryTransactionFactory.getDaoInstance(user).getCategoryByUUID(uuidParam);
    }


}
