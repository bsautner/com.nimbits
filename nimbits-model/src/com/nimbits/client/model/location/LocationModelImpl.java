/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.location;

import com.nimbits.client.exception.NimbitsException;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/4/12
 * Time: 9:20 AM
 */
public class LocationModelImpl implements Serializable, Location{

    private double _lat;
    private double _lng;


    public LocationModelImpl(double lat, double lng) {
        this._lat = lat;
        this._lng = lng;
    }

    public LocationModelImpl(String stringLoc) {
        String[] r = stringLoc.split(",");
        if (r.length == 2) {
            _lat = Double.valueOf(r[0].trim());
            _lng = Double.valueOf(r[1].trim());
        }
        else {
            _lat = 0.0;
            _lng =0.0;
        }

    }

    @Override
    public double getLat() {
        return _lat;
    }

    public void setLat(double _lat) {
        this._lat = _lat;
    }
    @Override
    public double getLng() {
        return _lng;
    }

    @Override
    public boolean isEmpty() {
        return _lat == 0.0 && _lng == 0.0;

    }

    public void setLng(double _lng) {
        this._lng = _lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationModelImpl that = (LocationModelImpl) o;

        if (Double.compare(that._lat, _lat) != 0) return false;
        if (Double.compare(that._lng, _lng) != 0) return false;

        return true;
    }

    @Override
    public String toString() {
        return _lat +", " + _lng;
    }
}
