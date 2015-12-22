package com.nimbits.client.model.socket;

import com.nimbits.client.model.entity.Entity;

import java.io.Serializable;

public interface Socket extends Entity, Serializable {
    String getTargetApiKey();

    String getTargetUrl();

    String getTargetPath();

    String getExtraParams();
}
