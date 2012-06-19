package com.nimbits.server.api.helper;

import com.nimbits.client.model.entity.Entity;


public interface LocationReportingHelper {
    void reportLocation(Entity entity, String location);
}
