package com.nimbits.client.model.socket;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;

public class SocketModel extends EntityModel implements Serializable, Socket {

    private String targetApiKey;
    private String targetUrl;
    private String targetPath;
    private String extraParams;

    public SocketModel(CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String uuid, String targetApiKey, String targetUrl, String targetPath, String extraParams) {
        super(name, description, entityType, protectionLevel, parent, owner, uuid);
        this.targetApiKey = targetApiKey;
        this.targetUrl = targetUrl;
        this.targetPath = targetPath;
        this.extraParams = extraParams;
    }

    public SocketModel(String targetApiKey, String targetUrl, String targetPath, String extraParams) {
        this.targetApiKey = targetApiKey;
        this.targetUrl = targetUrl;
        this.targetPath = targetPath;
        this.extraParams = extraParams;
    }

    public SocketModel(Entity anEntity, String targetApiKey, String targetUrl, String targetPath, String extraParams) {
        super(anEntity);
        this.targetApiKey = targetApiKey;
        this.targetUrl = targetUrl;
        this.targetPath = targetPath;
        this.extraParams = extraParams;
    }

    public SocketModel(Socket aSocket) {

        super(aSocket);
        this.targetApiKey = aSocket.getTargetApiKey();
        this.targetUrl = aSocket.getTargetUrl();
        this.targetPath = aSocket.getTargetPath();
        this.extraParams = aSocket.getExtraParams();
    }

    public SocketModel() {
    }

    @Override
    public String getTargetApiKey() {
        return targetApiKey;
    }

    @Override
    public String getTargetUrl() {
        return targetUrl;
    }

    @Override
    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public String getExtraParams() {
        return extraParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SocketModel that = (SocketModel) o;

        if (extraParams != null ? !extraParams.equals(that.extraParams) : that.extraParams != null) return false;
        if (!targetApiKey.equals(that.targetApiKey)) return false;
        if (targetPath != null ? !targetPath.equals(that.targetPath) : that.targetPath != null) return false;
        if (!targetUrl.equals(that.targetUrl)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + targetApiKey.hashCode();
        result = 31 * result + targetUrl.hashCode();
        result = 31 * result + (targetPath != null ? targetPath.hashCode() : 0);
        result = 31 * result + (extraParams != null ? extraParams.hashCode() : 0);
        return result;
    }


}
