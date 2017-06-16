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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;

@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)

@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Instance extends Entity implements Serializable {

    private final static EntityType entityType = EntityType.instance;

    @Persistent
    @JsonProperty("base_url")
    private String baseUrl;

    @Persistent
    private String apiKey;



    protected Instance(String name, String description, String parent, String owner, String baseUrl, String apiKey) {
        super(name, description, entityType, parent, owner);
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    private Instance() {
        super(entityType);
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Instance update = (Instance) entity;
        this.baseUrl = update.baseUrl;
        this.apiKey = update.apiKey;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public static class Builder extends EntityBuilder {

        private String baseUrl;

        private String password;



        public Builder name(String name) {
            this.name = CommonFactory.createName(name, entityType);
            return this;
        }

        public Instance create() {


            return new Instance(name, description, parent, owner,
                   baseUrl, password);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Instance e) {
            super.init(e);


            baseUrl = e.getBaseUrl();

            password = e.getApiKey();

            return this;
        }


        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder url(String url) {
            this.baseUrl = url;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }


    }

}
