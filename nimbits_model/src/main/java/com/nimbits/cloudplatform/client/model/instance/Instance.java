package com.nimbits.cloudplatform.client.model.instance;

import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: benjamin
 * Date: 5/22/12
 * Time: 3:03 PM
 * Copyright 2012 Nimbits Inc - All Rights Reserved
 */
public interface Instance  extends Entity, Serializable {
    int getId();

    String getBaseUrl();

    EmailAddress getOwnerEmail() ;

    String getVersion();

    Date getTs();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
