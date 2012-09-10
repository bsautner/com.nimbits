/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.api.openid;


import java.io.Serializable;

/**
 * Simple representation of an authenticated user.
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String claimedId;
    private String email;
    private String firstName;
    private String lastName;

    public UserInfo() {
    }

    public UserInfo(String claimedId, String email, String firstName,
                    String lastName) {
        this.claimedId = claimedId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getClaimedId() {
        return claimedId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
