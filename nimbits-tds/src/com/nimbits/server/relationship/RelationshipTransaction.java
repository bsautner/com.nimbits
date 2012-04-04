package com.nimbits.server.relationship;

import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.relationship.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 9:53 AM
 */
public interface RelationshipTransaction {

    Relationship createRelationship(Entity entity, String relatedEntityKey);
    Relationship getRelationship(Entity entity);
    Relationship getRelationship(String key);
}
