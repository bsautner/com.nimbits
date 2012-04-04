package com.nimbits.client.model.relationship;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 9:48 AM
 */
public class RelationshipModel implements Relationship {

    private String key;
    private String foreignKey;


    protected RelationshipModel() {
    }

    public RelationshipModel(Relationship relationship) {
        this.foreignKey = relationship.getForeignKey();
        this.key = relationship.getKey();
    }
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getForeignKey() {
        return foreignKey;
    }
}
