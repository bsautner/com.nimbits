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

package com.nimbits.mobile.ui.chart;

import android.content.Context;
import android.view.View;
import com.nimbits.client.model.value.Value;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 12/30/12
 * Time: 9:55 AM
 */
public interface Chart {

    /**
     * A constant for the name field in a list activity.
     */
    String NAME = "name";
    /**
     * A constant for the description field in a list activity.
     */
    String DESC = "desc";

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    String getName();

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    String getDesc();

    /**
     * Executes the chart demo.
     *
     * @param context        the context
     * @param entity
     * @param valuesResponse @return the built intent
     */
    View execute(Context context, String entity, List<Value> valuesResponse);

}
