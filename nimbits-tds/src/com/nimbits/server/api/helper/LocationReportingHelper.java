package com.nimbits.server.api.helper;

import com.nimbits.client.model.entity.Entity;

import javax.servlet.http.HttpServletRequest;


public interface LocationReportingHelper {
    void reportLocation(Entity entity, String location);

    void reportLocation(HttpServletRequest threadLocalRequest, Entity point);
}
