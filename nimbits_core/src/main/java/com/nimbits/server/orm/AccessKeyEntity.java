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

package com.nimbits.server.orm;

import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import org.apache.commons.lang3.StringUtils;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;



@PersistenceCapable()
public class AccessKeyEntity extends EntityStore implements AccessKey {


    @Persistent
    private String code;

    @Persistent @Deprecated
    private String scope;

    @Persistent @Deprecated
    @SuppressWarnings("unused")
    private boolean enabled;

    @Persistent @Deprecated
    private int authLevel;

    @SuppressWarnings("unused")
    protected AccessKeyEntity() {

    }

    public AccessKeyEntity(final AccessKey entity) {
        super(entity);
        this.code = entity.getCode();
        this.scope = "";
        this.authLevel = 0;
        this.enabled = true;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;

    }



    @Override
    public void update(final Entity update) {
        super.update(update);



    }

    @Override
    public void validate(User user) {
        super.validate(user);
        if (StringUtils.isEmpty(this.code)) {
            throw new IllegalArgumentException("Access Key must not be empty, you can delete the id if you don't want it anymore.");
        }



    }





}

