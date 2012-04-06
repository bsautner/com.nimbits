/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

package com.nimbits.client.model.point;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:06 PM
 */
public class PointModelFactory {


    private PointModelFactory() {
    }

    public static PointModel createPointModel() {

        return new PointModel();

    }



    public static PointModel createPointModel(final Entity p) throws NimbitsException {

        return new PointModel(p);

    }


}
