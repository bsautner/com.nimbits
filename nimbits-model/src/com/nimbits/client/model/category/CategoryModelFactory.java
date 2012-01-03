/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

package com.nimbits.client.model.category;

import com.nimbits.client.model.category.impl.CategoryModelImpl;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 10:47 AM
 */
public class CategoryModelFactory {

    public static Category createCategoryModel(final Category c) {

        return new CategoryModelImpl(c);

    }

    public static Category createCategoryModel(CategoryName categoryName) {

        return new CategoryModelImpl(categoryName);

    }

}
