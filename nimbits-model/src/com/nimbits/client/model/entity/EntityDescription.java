package com.nimbits.client.model.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.server.Server;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 4:50 PM
 */
public interface EntityDescription extends Serializable {
    int getIdPoint();

    Server getServer();

    String getUuid();

    String getName();

    String getDesc();

    EntityType getEntityType();


}
