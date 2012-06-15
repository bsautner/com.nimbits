package com.nimbits.server.service.entity;

import com.nimbits.client.exception.NimbitsException;


public interface EntityService {

    void processEntity(final String entityJson, final String actionText, final String instanceURL) throws NimbitsException;

}
