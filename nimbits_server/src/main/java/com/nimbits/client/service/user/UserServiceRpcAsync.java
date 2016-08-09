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

package com.nimbits.client.service.user;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.user.User;

public interface UserServiceRpcAsync {

    void doLogin(final String email, final String password, final AsyncCallback<User> async) throws UserServiceRpcException;

    void register(final String email, final String password, final AsyncCallback<User> async) throws UserServiceRpcException;

    void logout(AsyncCallback<Void> asyncCallback);

    void doForgotPassword(String value, AsyncCallback<Void> asyncCallback) throws UserServiceRpcException;

    void resetPassword(String email, String password, String recoveryToken, AsyncCallback<User> asyncCallback) throws UserServiceRpcException;

}
