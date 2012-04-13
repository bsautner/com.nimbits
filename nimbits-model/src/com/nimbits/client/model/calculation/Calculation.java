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

    void setEnabled(boolean b);

    @Override
    void update(Entity update) throws NimbitsException;

    void setZ(String z);

    void setTrigger(String trigger);

    void setY(String y);

    void setX(String x);

    void setFormula(String formula);

    void setTarget(String target);
}
