/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.cloudplatform.server.orm;

import com.nimbits.cloudplatform.client.model.category.Category;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/8/12
 * Time: 10:53 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class CategoryEntity extends EntityStore implements Category {

    private static final long serialVersionUID = -4488132572071199717L;

    @SuppressWarnings("unused")
    protected CategoryEntity() {
    }


    public CategoryEntity(final Entity entity)  {
        super(entity);

    }
//
//    @Override
//    public void update(Entity update)  {
//        super.update(update);
//    }
//
//    @Override
//    public void validate()  {
//        super.validate();
//    }
}
