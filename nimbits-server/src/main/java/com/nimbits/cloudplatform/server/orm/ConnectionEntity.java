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

package com.nimbits.cloudplatform.server.orm;

import com.nimbits.cloudplatform.client.model.connection.Connection;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/11/12
 * Time: 6:18 PM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ConnectionEntity extends EntityStore implements Connection {


    private static final long serialVersionUID = 653954371977705551L;

    @SuppressWarnings("unused")
    protected ConnectionEntity() {
    }

    public ConnectionEntity(final Entity entity)  {
        super(entity);

    }

}