package com.nimbits.android.ui.chart;

import android.content.Context;
import android.view.View;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.value.Value;

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
    View execute(Context context, Entity entity, List<Value> valuesResponse) throws Exception;

}
