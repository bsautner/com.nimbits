package com.nimbits.server.orm;




import com.google.appengine.api.datastore.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.relationship.*;

import javax.jdo.annotations.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 9:41 AM
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class RelationshipEntity implements Relationship {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key key;

    @Persistent
    private String foreignKey;


    public RelationshipEntity( Entity entity, String foreignKey) {

        this.key = KeyFactory.createKey(RelationshipEntity.class.getSimpleName(), entity.getKey());
        this.foreignKey = foreignKey;

    }

    @Override
    public String getKey() {
        return key.getName();
    }
    @Override
    public String getForeignKey() {
        return foreignKey;
    }
}
