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

package com.nimbits.server.orm.socket;

import com.nimbits.client.model.user.User;

import javax.jdo.annotations.*;
import java.util.Date;

/**
 * represents a current socket session
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SocketStore {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String email;

    @Persistent
    private String session;

    @Persistent
    private Date dateCreated;

    public SocketStore(User user) {
        this.email = user.getEmail().getValue();
        this.dateCreated = new Date();
        this.session = user.getToken();

    }

    public String getEmail() {
        return email;
    }

    public Date getDateCreated() {
        return new Date(dateCreated.getTime());
    }

    public String getSession() {
        return session;
    }
}
