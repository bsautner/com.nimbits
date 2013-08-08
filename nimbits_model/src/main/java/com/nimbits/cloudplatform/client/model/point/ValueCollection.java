package com.nimbits.cloudplatform.client.model.point;

import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;

import java.util.List;

/**
 * Created by benjamin on 8/6/13.
 */
public class ValueCollection {

    String pointId;
    List<ValueModel> values;

    public String getPointId() {
        return pointId;
    }

    public List<ValueModel> getValues() {
        return values;
    }
}
