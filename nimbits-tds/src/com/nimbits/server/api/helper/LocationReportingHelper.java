package com.nimbits.server.api.helper;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.location.Location;

import javax.servlet.http.HttpServletRequest;


public interface LocationReportingHelper {
    void reportLocation(Entity entity, Location location);

   // void reportLocation(HttpServletRequest threadLocalRequest, Entity point);
}
