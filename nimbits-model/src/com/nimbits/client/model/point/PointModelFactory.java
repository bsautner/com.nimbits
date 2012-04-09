/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

package com.nimbits.client.model.point;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:06 PM
 */
public class PointModelFactory {


    private PointModelFactory() {
    }

    public static PointModel createPointModel(final Entity entity) throws NimbitsException {

        return new PointModel(entity);

    }



    public static PointModel createPointModel(final Point point) throws NimbitsException {

        return new PointModel(point);

    }


    public static List<Point> createPointModels(final List<Point> result) throws NimbitsException {
        final List<Point> r = new ArrayList<Point>(result.size());
        for (final Point p : result) {
            r.add(createPointModel(p));
        }
        return r;
    }
}
