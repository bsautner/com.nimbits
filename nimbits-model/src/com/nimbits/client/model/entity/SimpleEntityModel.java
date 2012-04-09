package com.nimbits.client.model.entity;

import com.nimbits.client.exception.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/9/12
 * Time: 2:17 PM
 */
public class SimpleEntityModel extends EntityModel implements Simple {

    protected SimpleEntityModel() {

    }

    public SimpleEntityModel(Entity entity) throws NimbitsException {
        super(entity);
    }
}
