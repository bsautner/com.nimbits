package com.nimbits.cloudplatform.helper;

import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueFactory;
import com.nimbits.cloudplatform.transaction.Transaction;
import org.apache.commons.lang3.Range;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 7:30 PM
 */
public class ValueHelper {

    public static Value recordValue(String name, double value)  {
        Value vx = ValueFactory.createValueModel(value);
        Point point = PointHelper.getPoint(name);

            List<Value> response = Transaction.postValue(point, vx);
            if (response.isEmpty()) {
                throw new RuntimeException("Record Value Failed");

            }
            else {
                return response.get(0);
            }

    }

    public static List<Value> getSeries(String name, int count)   {
        Point point = PointHelper.getPoint(name);

            return Transaction.getSeries(point, Range.between(0, count));

    }

}
