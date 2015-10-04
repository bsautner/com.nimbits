package com.nimbits.client.model.webhook;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
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



    public WebHookModel(final CommonIdentifier name,
                        final String description,
                        final String parent
                , HttpMethod method,
                        UrlContainer url, boolean enabled, String downloadTarget) {
        super(name, description, EntityType.webhook, ProtectionLevel.everyone, parent, null, null);
        this.method = method.getCode();
        this.url = url.getUrl();
        this.enabled = enabled;
        this.downloadTarget = downloadTarget;
    }

    public WebHookModel(WebHook webHook) {
        super(webHook);
        this.method = webHook.getMethod().getCode();
        this.url = webHook.getUrl().getUrl();
        this.enabled = webHook.isEnabled();
        this.downloadTarget = webHook.getDownloadTarget();
    }
    protected WebHookModel() {
    }


    @Override
    public HttpMethod getMethod() {
        return HttpMethod.lookup(this.method);
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
        private UrlContainer url;


        private String downloadTarget;


        public Builder setMethod(HttpMethod method) {
            this.method = method;
            return this;
        }


        public Builder setUrl(String url) {
            this.url = UrlContainer.getInstance(url);
            return this;
        }

        public Builder name(String name) {
            this.name(CommonFactory.createName(name, EntityType.webhook));
            return this;

        }



        public Builder setDownloadTarget(String downloadTarget) {
            this.downloadTarget = downloadTarget;
            return this;
        }


        public WebHook create() {
            return new WebHookModel(name, description, parent, method, url, true, downloadTarget );
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        @Override
        public Builder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }
        @Override
        public Builder key(String key) {
            this.key = key;
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
        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }
    }

}
