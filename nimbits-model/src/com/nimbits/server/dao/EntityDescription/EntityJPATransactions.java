package com.nimbits.server.dao.EntityDescription;

import com.nimbits.client.model.entity.EntityDescription;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public interface EntityJPATransactions {
    EntityDescription addEntityDescription(final EntityDescription entityDescription);

    EntityDescription addUpdateEntityDescription(final EntityDescription entityDescription);

    List<EntityDescription> searchEntityDescription(final String searchText);

    EntityDescription getEntityDescriptionByUUID(final String uuid);

    void deleteEntityDescriptionByUUID(final String uuid);


}
