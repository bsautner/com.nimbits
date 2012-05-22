package com.nimbits.client.model.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 9:51 AM
 */
public class EntityModelFactory {


    private EntityModelFactory() {
    }

    public static Entity createEntity(final User user, final Entity entity) throws NimbitsException {
        final Entity r = new EntityModel(entity);
        final boolean isOwner =  (user != null && entity.getOwner().equals(user.getKey()));
        r.setReadOnly(!isOwner);
        return r;

    }

    public static Entity createEntity(final Entity entity) throws NimbitsException {

        return new EntityModel(entity);

    }

    public static Entity createEntity(final EntityName name,
                                      final String description,
                                      final EntityType entityType,
                                      final ProtectionLevel protectionLevel,
                                      final String parentUUID,
                                      final String ownerUUID,
                                      final String uuid ) {
        return new EntityModel(name, description, entityType, protectionLevel,  parentUUID,
                ownerUUID, uuid);
    }



    public static Entity createEntity(final EntityName name,
                                      final String description,
                                      final EntityType entityType,
                                      final ProtectionLevel protectionLevel,
                                      final String parentUUID,
                                      final String ownerUUID) {
        return new EntityModel(name, description, entityType, protectionLevel,  parentUUID,
                ownerUUID, "");
    }
    public static Entity createEntity(final User user) throws NimbitsException {
        final EntityName name = CommonFactoryLocator.getInstance().createName(user.getEmail().getValue(), EntityType.user);

        return createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                user.getKey(), user.getKey(),null);

    }






    public static Entity createEntity(final EntityName name, final EntityType entityType) {
        return new EntityModel(name,
                "",
                entityType,
                ProtectionLevel.everyone,
                null,

                null,
                null);
    }


    public static Entity createEntity(final Entity user, final EntityName name) {
        return user != null ? new EntityModel(name,
                "",
                EntityType.point,
                ProtectionLevel.everyone,
                user.getKey(),
                user.getKey(),
                user.getKey())

                :

                new EntityModel(name,
                "",
                EntityType.point,
                ProtectionLevel.everyone,
                null,
                null,null

                );
    }



}
