package com.nimbits.client.model.point;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:52 PM
 */
public interface Calculation extends Serializable {


    String getFormula();

    Boolean getEnabled();

    String getTarget();

    String getX();

    String getY();

    String getZ();
}
