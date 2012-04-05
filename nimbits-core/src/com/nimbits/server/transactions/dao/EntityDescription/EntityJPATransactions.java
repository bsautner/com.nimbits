package com.nimbits.server.transactions.dao.EntityDescription;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.EntityDescription;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public interface EntityJPATransactions {
    EntityDescription addEntityDescription(final EntityDescription entityDescription) throws NimbitsException;

    EntityDescription addUpdateEntityDescription(final EntityDescription entityDescription) throws NimbitsException;

    List<EntityDescription> searchEntityDescription(final String searchText);

    EntityDescription getEntityDescriptionByUUID(final String uuid) throws NimbitsException;

    void deleteEntityDescriptionByUUID(final String uuid);


}
