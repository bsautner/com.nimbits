package com.nimbits.client.model.calculation;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:52 PM
 */
public interface Calculation extends Entity, Serializable {

    String getTrigger();

    String getFormula();

    Boolean getEnabled();

    String getTarget();

    String getX();

    String getY();

    String getZ();

    void setEnabled(final boolean b);

    @Override
    void update(final Entity update) throws NimbitsException;

    void setZ(final String z);

    void setTrigger(final String trigger);

    void setY(final String y);

    void setX(final String x);

    void setFormula(final String formula);

    void setTarget(final String target);
}
