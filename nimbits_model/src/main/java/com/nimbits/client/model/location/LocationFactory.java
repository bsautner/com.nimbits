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

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/4/12
 * Time: 9:24 AM
 */
public class LocationFactory {



    public static Location createLocation(String locationString) {

        return new LocationModelImpl(locationString);

    }
    public static Location createLocation( ) {

        return new LocationModelImpl(0.0, 0.0);

    }

    public static Location createLocation(double lt, double lg) {

        return new LocationModelImpl(lt, lg);
    }
}
