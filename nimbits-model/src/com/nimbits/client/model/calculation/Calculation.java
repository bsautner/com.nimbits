package com.nimbits.client.model.calculation;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:52 PM
 */
public interface Calculation extends Serializable {

    String getUUID();

    String getTrigger();

    String getFormula();

    Boolean getEnabled();

    String getTarget();

    String getX();

    String getY();

    String getZ();

    void setEnabled(boolean b);
}
