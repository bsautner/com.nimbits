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


import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;

import java.io.Serializable;
import java.util.Date;


public interface User extends Entity, Serializable {

    String getPassword();

    String getPasswordSalt();

    UserSource getSource();

    EmailAddress getEmail();

    boolean getIsAdmin();

    void setIsAdmin(boolean userAdmin);

    void setToken(String authToken);

    String getToken();

    void setEmail(EmailAddress emailAddress);

    void setLoginInfo(LoginInfo loginInfo);

    LoginInfo getLoginInfo();


    void setPasswordResetToken(String token);

    void setPasswordResetTimestamp(Long date);

    String getPasswordResetToken();

    Long getPasswordResetTimestamp();

    void setPasswordSalt(String passwordSalt);

    void setPassword(String cryptPassword);

}
