/*
 * Copyright (c) 2011. Nimbits Inc. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

package com.nimbits.client.model.user;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;

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

    public static List<User> createUserModels(final Collection<User> users) throws NimbitsException {
        List<User> retObj = new ArrayList<User>(users.size());

        for (final User u : users) {
            retObj.add(createUserModel(u));
        }

        return retObj;
    }

    public static User createUserModel(final User u) throws NimbitsException {
        return new UserModel(u);
    }

    public static User createUserModel(final Entity entity) throws NimbitsException {

        return new UserModel(entity);
    }



}
