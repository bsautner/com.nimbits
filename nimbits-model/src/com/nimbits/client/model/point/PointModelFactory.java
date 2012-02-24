/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

package com.nimbits.client.model.point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:06 PM
 */
public class PointModelFactory {





    public static PointModel createPointModel(final Point p) {

        return new PointModel(p);

    }


    public static List<Point> createPointModels(final List<Point> points) {
        final List<Point> retObj = new ArrayList<Point>();

        for (final Point p : points) {
            retObj.add(createPointModel(p));
        }

        return retObj;


    }



}
