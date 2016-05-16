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

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class WebHookEntity extends EntityStore implements WebHook  {

    @Persistent
    private Integer method;

    @Persistent
    private String url;

    @Persistent
    private boolean enabled;

    @Persistent
    private String downloadTarget;

    @Persistent
    private Integer pathChannel;

    @Persistent
    private Integer bodyChannel;



    public WebHookEntity(WebHook webHook) {
        super(webHook);
        this.method = webHook.getMethod().getCode();
        this.url = webHook.getUrl().getUrl();
        this.enabled = webHook.isEnabled();
        this.downloadTarget = webHook.getDownloadTarget();
        this.pathChannel = webHook.getPathChannel().getCode();
        this.bodyChannel = webHook.getBodyChannel().getCode();
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
    public void init(Entity anEntity) {

    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.lookup(this.method);
    }

    @Override
    public DataChannel getPathChannel() {
        return pathChannel == null ? DataChannel.none : DataChannel.lookup(pathChannel);
    }

    @Override
    public void setPathChannel(DataChannel dataChannel) {
        this.pathChannel = dataChannel.getCode();
    }

    @Override
    public DataChannel getBodyChannel() {
        return bodyChannel == null ? DataChannel.data : DataChannel.lookup(bodyChannel);
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



}
