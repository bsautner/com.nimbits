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

package com.nimbits.cloudplatform.client.model.user;

import com.nimbits.cloudplatform.client.model.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 4:10 PM
 */
public class UserModelFactory {

    private UserModelFactory() {
    }

    public static List<User> createUserModels(final Collection<User> users)  {
        List<User> retObj = new ArrayList<User>(users.size());

        for (final User u : users) {
            retObj.add(createUserModel(u));
        }

        return retObj;
    }

    public static User createUserModel(final User u)  {
        return new UserModel(u);
    }

    public static User createUserModel(final Entity entity)  {

        return new UserModel(entity);
    }



}
