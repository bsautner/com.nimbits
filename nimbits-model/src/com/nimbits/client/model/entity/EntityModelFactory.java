package com.nimbits.client.model.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.*;

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
                                      final String blobKey) {
        return new EntityModel(name, description, entityType, protectionLevel,  parentUUID,
                ownerUUID, blobKey);
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
                user.getKey(), user.getKey());

    }


    public static EntityDescription createEntityDescription(final Server server, final Entity entity) throws NimbitsException {
        return new EntityDescriptionModel(server, entity);
    }

    public static List<EntityDescription> createPointDescriptions(final Collection<EntityDescription> entityDescriptions) throws NimbitsException {
        final List<EntityDescription> retObj = new ArrayList<EntityDescription>(entityDescriptions.size());
        for (final EntityDescription entityDescription : entityDescriptions) {
            retObj.add(createPointDescription(entityDescription));
        }
        return retObj;

    }

    public static EntityDescription createPointDescription(final EntityDescription entityDesc) throws NimbitsException {

        return new EntityDescriptionModel(entityDesc);


    }

    public static List<Entity> createEntities(final User user,final Collection<Entity> result) throws NimbitsException {
        final List<Entity> entities = new ArrayList<Entity>(result.size());
        for (final Entity e : result) {

            final boolean isOwner = (user != null) && e.getOwner().equals(user.getKey());

            if (entityIsReadable(user, e, isOwner))
            {

                final Entity r = createEntity(user, e);

                entities.add(r);
            }

        }
        return entities;

    }

    private static boolean entityIsReadable(final User user, final Entity e, final boolean owner) {
        boolean retVal =  ((e.getEntityType().equals(EntityType.user) ||
                owner ||
                e.getProtectionLevel().equals(ProtectionLevel.everyone) ||
                e.getProtectionLevel().equals(ProtectionLevel.onlyConnection))

        );

        if (e.getEntityType().equals(EntityType.userConnection) && ! e.getOwner().equals(user.getKey())) {
            retVal = false;
        }
        if (e.getEntityType().equals(EntityType.summary) && user == null) {
            retVal = true; //this is a system request from the summary cron job.
        }
        return retVal;


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


    public static Entity createEntity(final User u, final EntityName name) {
        return u != null ? new EntityModel(name,
                "",
                EntityType.point,
                ProtectionLevel.everyone,
                u.getKey(),
                u.getKey(),
                u.getKey()) : new EntityModel(name,
                "",
                EntityType.point,
                ProtectionLevel.everyone,

                null,
                null,
                null);
    }



}
