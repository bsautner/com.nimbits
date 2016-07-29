/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.model.user;

import com.nimbits.client.constants.Const;


public class UserModelFactory {


    private UserModelFactory() {
    }

    public static LoginInfo createLoginInfo(String loginUrl, String logoutUrl, UserStatus userStatus) {
        return new LoginInfoImpl(loginUrl, logoutUrl, userStatus);
    }

    public static LoginInfo createNullLoginInfo() {
        return new LoginInfoImpl("", Const.WEBSITE, UserStatus.unknown);
    }


}
