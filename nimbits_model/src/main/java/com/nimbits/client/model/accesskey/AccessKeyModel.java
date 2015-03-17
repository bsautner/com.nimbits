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

package com.nimbits.client.model.accesskey;

import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;

/**
 *
 * The POJO that represents access keys entities stored in the database
 *
 *
 */
public class AccessKeyModel extends EntityModel implements AccessKey, Serializable {

    private String code;
    private String scope;
    private int authLevel;

    @SuppressWarnings("unused")
    private AccessKeyModel() {
    }

    public AccessKeyModel(final AccessKey anEntity) {
        super(anEntity);
        this.code = anEntity.getCode();
        this.scope = anEntity.getScope();
        this.authLevel = anEntity.getAuthLevel().getCode();

    }

    @Override
    public String getCode() {
        return code;
    }

    public AccessKeyModel(final Entity anEntity, final String code, final String scope, final AuthLevel level) {
        super(anEntity);
        this.code = code;
        this.scope = scope;
        this.authLevel = level.getCode();
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.get(this.authLevel);
    }

    @Override
    public void setAuthLevel(AuthLevel level) {
        this.authLevel = level.getCode();
    }
}
