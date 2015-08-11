package com.nimbits.client.model.webhook;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;

public class WebHookModel extends EntityModel implements Serializable, WebHook {


    private int method;
    private String url;
    private boolean enabled;
    private String downloadTarget;



    public WebHookModel(final CommonIdentifier name,
                        final String description,
                        final String parent
                , HttpMethod method, UrlContainer url, boolean enabled, String downloadTarget) {
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


}
