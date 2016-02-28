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

package com.nimbits.client.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ServicePayload<T extends Serializable> implements Serializable {

    @Expose
    private String email;

    private String token;

    @Expose
    private String nextRecord;
    @Expose
    private T payload;

    public ServicePayload(String email, String token, T payload) {
        this.email = email;
        this.token = token;
        this.payload = payload;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public T getPayload() {
        return payload;
    }
}
