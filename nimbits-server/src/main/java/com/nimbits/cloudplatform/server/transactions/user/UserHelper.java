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

package com.nimbits.cloudplatform.server.transactions.user;

import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: Benjamin Sautner
 * Date: 1/1/13
 * Time: 6:50 PM
 */
public class UserHelper {

    public static final ThreadLocal req = new ThreadLocal();

    public static void init(HttpServletRequest s){
        req.set(s);
    }
    public static HttpServletRequest get(){
        return (HttpServletRequest)req.get();
    }

    public static User getUser()  {

       try {
            return UserTransaction.getHttpRequestUser(get());
        } catch (Exception e) {
           LogHelper.logException(UserHelper.class, e);
            return UserTransaction.getAnonUser();
        }

    }
}
