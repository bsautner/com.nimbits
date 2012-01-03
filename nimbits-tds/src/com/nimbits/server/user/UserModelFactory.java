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

package com.nimbits.server.user;

import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.orm.NimbitsUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 4:10 PM
 */
public class UserModelFactory {
    public static UserModel createUserModel(final long id) {
        return new UserModel(id);
    }

    public static List<User> createUserModels(final List<NimbitsUser> users) {
        List<User> retObj = new ArrayList<User>();

        for (final NimbitsUser u : users) {
            retObj.add(createUserModel(u));
        }

        return retObj;
    }

    public static UserModel createUserModel(final NimbitsUser u) {
        return new UserModel(u);
    }
}
