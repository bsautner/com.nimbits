package com.nimbits.client.model.schedule;

import com.nimbits.client.model.entity.Entity;

import java.io.Serializable;

public interface Schedule extends Entity, Serializable {
    Boolean isEnabled();

    Long getInterval();

    String getSource();

    String getTarget();

    void setLastProcessed(Long lastProcessed);

    Long getLastProcessed();

    void setEnabled(Boolean enabled);

    void setInterval(Long interval);

    void setSource(String source);

    void setTarget(String target);
}
