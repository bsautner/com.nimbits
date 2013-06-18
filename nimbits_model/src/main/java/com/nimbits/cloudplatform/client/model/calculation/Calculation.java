package com.nimbits.cloudplatform.client.model.calculation;

import com.nimbits.cloudplatform.client.model.trigger.*;

import java.io.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:52 PM
 */
public interface Calculation extends Trigger, Serializable {

    String getFormula();

    String getX();

    String getY();

    String getZ();

    void setZ(final String z);

    void setY(final String y);

    void setX(final String x);

    void setFormula(final String formula);

}
