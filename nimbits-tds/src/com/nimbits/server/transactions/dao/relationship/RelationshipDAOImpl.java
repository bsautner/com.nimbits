package com.nimbits.server.transactions.dao.relationship;

import com.nimbits.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.relationship.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.relationship.*;
import org.datanucleus.exceptions.*;

import javax.jdo.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 9:53 AM
 */
public class RelationshipDAOImpl implements RelationshipTransaction {

    public Relationship createRelationship(final Entity entity, final String relatedEntityKey) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try{
            final Relationship re = new RelationshipEntity(entity, relatedEntityKey);
            pm.makePersistent(re);
            return RelationshipFactory.createRelationship(re);
        }
        finally {
            pm.close();
        }


    }

    @Override
    public Relationship getRelationship(Entity entity) {
       return getRelationship(entity.getKey());

    }
    @Override
    public Relationship getRelationship(String key) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {

            Relationship r =   pm.getObjectById(RelationshipEntity.class, key);
            return RelationshipFactory.createRelationship(r);

        }
        catch (JDOObjectNotFoundException ex) {
            return null; //todo delete entity
        }
        finally {
            pm.close();
        }

    }
}
