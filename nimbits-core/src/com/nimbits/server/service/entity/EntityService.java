package com.nimbits.server.service.entity;

import com.nimbits.client.exception.NimbitsException;

import java.util.List;


public interface EntityService {

    void processEntity(final String entityJson, final String actionText, final String instanceURL) throws NimbitsException;
    void processLocation(final String entityJson, final String location) throws NimbitsException;
    List<String[]> getLocations();

}
