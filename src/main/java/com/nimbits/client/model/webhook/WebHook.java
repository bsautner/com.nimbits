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

package com.nimbits.client.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.CommonFactory;
import com.nimbits.client.model.Entity;
import com.nimbits.client.model.Listener;
import com.nimbits.client.model.topic.Topic;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@PersistenceCapable
public class WebHook extends Listener implements Serializable {

    private final static EntityType entityType = EntityType.webhook;

    @Persistent
    private int method;

    @Persistent
    private String url;

    @Persistent
    private int bodyChannel;

    @Persistent
    private int pathChannel;


    protected WebHook(String name, String description, String parent, String owner, int method, String url, boolean enabled, List<Topic> targets, List<Topic> triggers, int bodyChannel, int pathChannel, boolean execute) {
        super(name, description, entityType, parent, owner, targets, triggers, enabled, execute);
        this.method = method;
        this.url = url;
        this.bodyChannel = bodyChannel;
        this.pathChannel = pathChannel;
    }

    public WebHook() {
        super(entityType);
    }


    @Override
    public void update(Entity entity) {
        super.update(entity);
        WebHook update = (WebHook) entity;
        this.method = update.getMethod().getCode();
        this.url = update.getUrl();
        this.pathChannel = update.getPathChannel().getCode();
        this.bodyChannel = update.getBodyChannel().getCode();
    }


    public HttpMethod getMethod() {
        return HttpMethod.lookup(this.method);
    }


    public DataChannel getPathChannel() {
        return DataChannel.lookup(pathChannel);
    }


    public void setPathChannel(DataChannel dataChannel) {
        this.pathChannel = dataChannel.getCode();
    }


    public DataChannel getBodyChannel() {
        return DataChannel.lookup(bodyChannel);
    }


    public void setBodyChannel(DataChannel dataChannel) {
        this.bodyChannel = dataChannel.getCode();

    }


    public String getUrl() {
        return url;
    }


    public void setMethod(HttpMethod method) {
        this.method = method.getCode();
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public static class Builder extends TriggerBuilder {

        private HttpMethod method;
        private DataChannel bodyChannel;
        private DataChannel pathChannel;
        private String url;


        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder bodyChannel(DataChannel channel) {
            this.bodyChannel = channel;
            return this;
        }

        public Builder pathChannel(DataChannel channel) {
            this.pathChannel = channel;
            return this;
        }


        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, EntityType.webhook);
            return this;

        }


        @Override
        public Builder targets(List v) {
            if (this.targets == null) {
                this.targets = new ArrayList<Topic>(v.size());
            }
            this.targets.addAll(v);
            return this;
        }

        @Override
        public Builder triggers(List v) {
            if (this.triggers == null) {
                this.triggers = new ArrayList(v.size());
            }
            this.triggers.addAll(v);
            return this;
        }

        @Override
        public Builder target(Topic v) {
            if (this.targets == null) {
                this.targets = new ArrayList<Topic>();
            }
            this.targets.add(v);

            return this;
        }

        @Override
        public Builder trigger(Topic v) {
            if (this.triggers == null) {
                this.triggers = new ArrayList<Topic>();
            }
            this.triggers.add(v);
            return this;
        }

        public WebHook create() {

            if (pathChannel == null) {
                pathChannel = DataChannel.none;
            }

            if (bodyChannel == null) {
                bodyChannel = DataChannel.none;
            }


            return new WebHook(name, description, parent, owner, method.getCode(), url,
                    enabled, targets, triggers, bodyChannel.getCode(), pathChannel.getCode(), execute);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(WebHook e) {
            super.init(e);
            method = e.getMethod();
            bodyChannel = e.getBodyChannel();
            pathChannel = e.getPathChannel();
            enabled = e.isEnabled();

            url = e.getUrl();


            return this;
        }


        public Builder enabled(boolean v) {
            this.enabled = v;
            return this;
        }


        public Builder execute(boolean execute) {
            this.execute = execute;
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


        public Builder enabled(Boolean value) {
            this.enabled = value;
            return this;
        }
    }

}
