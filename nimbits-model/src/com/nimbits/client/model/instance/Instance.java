package com.nimbits.client.model.instance;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 3:03 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
public interface Instance  extends Entity, Serializable {
    int getId();

    String getBaseUrl();

    EmailAddress getOwnerEmail() throws NimbitsException;

    String getVersion();

    Date getTs();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
