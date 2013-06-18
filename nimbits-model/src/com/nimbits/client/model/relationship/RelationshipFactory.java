package com.nimbits.client.model.relationship;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 9:49 AM
 */
public class RelationshipFactory {

    protected RelationshipFactory() {

    }

    public static Relationship createRelationship(Relationship relationship) {

        return new RelationshipModel(relationship);

    }
    public static List<Relationship> createRelationships(List<Relationship> relationships) {
        List<Relationship> retObj = new ArrayList<Relationship>(relationships.size());
        for (Relationship r : relationships) {
            retObj.add(createRelationship(r));
        }
        return retObj;

    }


}
