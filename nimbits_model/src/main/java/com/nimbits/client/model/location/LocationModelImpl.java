/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.location;

import java.io.Serializable;


public class LocationModelImpl implements Serializable, Location {

    private Double _lat;
    private Double _lng;


    public LocationModelImpl(Double lat, Double lng) {
        this._lat = lat;
        this._lng = lng;
    }

    public LocationModelImpl(String stringLoc) {
        String[] r = stringLoc.split(",");
        if (r.length == 2) {
            _lat = Double.valueOf(r[0].trim());
            _lng = Double.valueOf(r[1].trim());
        } else {
            _lat = null;
            _lng = null;
        }

    }

    @Override
    public Double getLat() {
        return _lat;
    }

    public void setLat(double _lat) {
        this._lat = _lat;
    }

    @Override
    public Double getLng() {
        return _lng;
    }

    @Override
    public boolean isEmpty() {
        return _lat == null && _lng == null;

    }

    public void setLng(double _lng) {
        this._lng = _lng;
    }

    @Override
    public String toString() {
        return _lat + ", " + _lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationModelImpl that = (LocationModelImpl) o;

        if (_lat != null ? !_lat.equals(that._lat) : that._lat != null) return false;
        if (_lng != null ? !_lng.equals(that._lng) : that._lng != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _lat != null ? _lat.hashCode() : 0;
        result = 31 * result + (_lng != null ? _lng.hashCode() : 0);
        return result;
    }
}
