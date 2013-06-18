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

package com.nimbits.cloudplatform.server.orm;

import com.nimbits.cloudplatform.client.enums.AuthLevel;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.shared.Utils;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 2:39 PM
 */

@PersistenceCapable()
public class AccessKeyEntity extends EntityStore implements AccessKey {


    private static final long serialVersionUID = 5218131660944424648L;
    @Persistent
    private String code;

    @Persistent
    private String scope;

    @Persistent
    @SuppressWarnings("unused")
    private boolean enabled;

    private int authLevel;

    @SuppressWarnings("unused")
    protected AccessKeyEntity() {

    }

    public AccessKeyEntity(final AccessKey entity)  {
        super(entity);
        this.code = entity.getCode();
        this.scope = entity.getScope();
        this.enabled = true;
        this.authLevel = entity.getAuthLevel().getCode();

    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;

    }

    @Override
    public void update(final Entity update)  {
        super.update(update);

        final AccessKey k = (AccessKey)update;
        this.code = k.getCode();
        this.scope = k.getScope();
        this.enabled = true;
        this.authLevel = k.getAuthLevel().getCode();

    }

    @Override
    public void validate(User user)  {
        super.validate(user);
        if (Utils.isEmptyString(this.code)) {
            throw new IllegalArgumentException("Access Key must not be empty, you can delete the key if you don't want it anymore.");
        }

        if (Utils.isEmptyString(this.scope)) {
            throw new IllegalArgumentException("Source must not be empty");
        }

        if (AuthLevel.get(this.authLevel).equals(AuthLevel.readPoint) ||
                AuthLevel.get(this.authLevel).equals(AuthLevel.readWritePoint)) {
            if (Utils.isEmptyString(this.scope)) {
                throw new IllegalArgumentException("Auth Keys with an auth level of point, must have a target point key set");

            }
        }
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(final String scope) {
        this.scope = scope;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.get(this.authLevel);
    }

    @Override
    public void setAuthLevel(final AuthLevel level) {
       this.authLevel = level.getCode();
    }


}

