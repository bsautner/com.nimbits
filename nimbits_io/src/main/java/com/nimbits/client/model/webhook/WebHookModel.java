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

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;

public class WebHookModel extends EntityModel implements Serializable, WebHook {


    @Expose
    private int method;
    @Expose
    private String url;
    @Expose
    private boolean enabled;
    @Expose
    private String downloadTarget;

    @Expose
    private int bodyChannel;


    @Expose
    private int pathChannel;


    protected WebHookModel(String id, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, int method, String url, boolean enabled, String downloadTarget, int bodyChannel, int pathChannel) {
        super(id, name, description, entityType, protectionLevel, parent, owner);
        this.method = method;
        this.url = url;
        this.enabled = enabled;
        this.downloadTarget = downloadTarget;
        this.bodyChannel = bodyChannel;
        this.pathChannel = pathChannel;
    }

    private WebHookModel() {

    }

    @Override
    public void update(Entity update) {
        super.update(update);
        WebHook webHook = (WebHook) update;
        this.method = webHook.getMethod().getCode();
        this.url = webHook.getUrl().getUrl();
        this.enabled = webHook.isEnabled();
        this.downloadTarget = webHook.getDownloadTarget();
        this.pathChannel = webHook.getPathChannel().getCode();
        this.bodyChannel = webHook.getBodyChannel().getCode();
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.lookup(this.method);
    }

    @Override
    public DataChannel getPathChannel() {
        return DataChannel.lookup(pathChannel);
    }

    @Override
    public void setPathChannel(DataChannel dataChannel) {
        this.pathChannel = dataChannel.getCode();
    }

    @Override
    public DataChannel getBodyChannel() {
        return DataChannel.lookup(bodyChannel);
    }

    @Override
    public void setBodyChannel(DataChannel dataChannel) {
        this.bodyChannel = dataChannel.getCode();

    }


    @Override
    public UrlContainer getUrl() {
        return UrlContainer.getInstance(url);
    }
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public void setMethod(HttpMethod method) {
        this.method = method.getCode();
    }
    @Override
    public void setUrl(UrlContainer url) {
        this.url = url.getUrl();
    }
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getDownloadTarget() {
        return downloadTarget;
    }

    @Override
    public void setDownloadTarget(String downloadTarget) {
        this.downloadTarget = downloadTarget;
    }



    public static class Builder extends EntityBuilder  {

        private HttpMethod method;
        private DataChannel bodyChannel;
        private DataChannel pathChannel;
        private Boolean enabled;
        private UrlContainer url;


        private String downloadTarget;


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
            this.url = UrlContainer.getInstance(url);
            return this;
        }

        public Builder name(String name) {
            this.name(CommonFactory.createName(name, EntityType.webhook));
            return this;

        }



        public Builder downloadTarget(String downloadTarget) {
            this.downloadTarget = downloadTarget;
            return this;
        }
        public Builder downloadTarget(Entity downloadTargetEntity) {
            this.downloadTarget = downloadTargetEntity.getId();
            return this;
        }


        public WebHook create() {
            if (protectionLevel == null) {
                protectionLevel = ProtectionLevel.everyone;
            }
            if (enabled == null) {
                enabled = true;
            }

            if (pathChannel == null) {
                pathChannel = DataChannel.none;
            }

            if (bodyChannel == null) {
                bodyChannel = DataChannel.none;
            }




            return new WebHookModel(id, name, description, EntityType.webhook, protectionLevel, parent, owner, method.getCode(), url.getUrl(), true,
                    downloadTarget, bodyChannel.getCode(), pathChannel.getCode());
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        private void initEntity(Entity anEntity) {


            this.id = anEntity.getId();
            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.entityType = anEntity.getEntityType();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();
            this.protectionLevel = anEntity.getProtectionLevel();
            this.alertType = anEntity.getAlertType().getCode();


        }

        public Builder init(WebHook e) {
            initEntity(e);
            method = e.getMethod();
            bodyChannel = e.getBodyChannel();
            pathChannel = e.getPathChannel();
            enabled = e.isEnabled();

            url = e.getUrl();
            downloadTarget = e.getDownloadTarget();

            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        @Override
        public Builder protectionLevel(ProtectionLevel protectionLevel) {
            this.protectionLevel = protectionLevel;
            return this;
        }
        @Override
        public Builder alertType(int alertType) {
            this.alertType = alertType;
            return this;
        }
        @Override
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }
        @Override
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }
        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }


        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder enabled(Boolean value) {
            this.enabled = value;
            return this;
        }
    }

}
