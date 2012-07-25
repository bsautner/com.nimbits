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

package com.nimbits.server.transactions.service.user;


import com.nimbits.client.service.user.*;

public class UserServiceFactory {

    private UserServiceFactory() {
    }


    private static class UserServiceHolder {
        static final UserService instance = new UserServiceImpl();

        private UserServiceHolder() {
        }
    }

    public static UserService getInstance() {
        return UserServiceHolder.instance;
    }

    private static class UserServerServiceHolder {
        static final UserServerService serverInstance = new UserServiceImpl();

        private UserServerServiceHolder() {
        }
    }

    public static UserServerService getServerInstance() {
        return UserServerServiceHolder.serverInstance;
    }
}
