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

package com.nimbits.server.orm;

import com.nimbits.client.model.Server;
import com.nimbits.client.model.entity.Entity;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/8/12
 * Time: 10:53 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ServerEntity extends EntityStore implements Server {

    private static final long serialVersionUID = -4488132572071199717L;
    private String url;
    private long id;
    private String apiKey;
    @SuppressWarnings("unused")
    protected ServerEntity() {
    }


    public ServerEntity(final Entity entity)  {
        super(entity);

    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }
//
//    @Override
//    public void update(Entity update)  {
//        super.update(update);
//    }
//
//    @Override
//    public void validate()  {
//        super.validate();
//    }
}
