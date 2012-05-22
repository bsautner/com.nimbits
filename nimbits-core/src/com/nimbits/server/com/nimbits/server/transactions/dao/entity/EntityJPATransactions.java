package com.nimbits.server.com.nimbits.server.transactions.dao.entity;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.Entity;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public interface EntityJPATransactions {
    Entity addEntity(final Entity entityDescription) throws NimbitsException;

    Entity addUpdateEntity(final Entity entityDescription) throws NimbitsException;

    List<Entity> searchEntity(final String searchText);

    Entity getEntityByUUID(final String uuid) throws NimbitsException;

    void deleteEntityByUUID(final String uuid);


}
