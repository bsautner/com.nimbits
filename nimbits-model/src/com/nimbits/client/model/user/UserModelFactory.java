/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

package com.nimbits.client.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 4:10 PM
 */
public class UserModelFactory {
    public static User createUserModel(final long id) {
        return new UserModel(id);
    }

    public static List<User> createUserModels(final List<User> users) {
        List<User> retObj = new ArrayList<User>();

        for (final User u : users) {
            retObj.add(createUserModel(u));
        }

        return retObj;
    }

    public static User createUserModel(final User u) {
        return new UserModel(u);
    }



    public List<User> createUserModels(final Set<User> users) {

        final List<User> retObj = new ArrayList<User>();

        for (final User u : users) {
            retObj.add(createUserModel(u));
        }

        return retObj;


    }

}
