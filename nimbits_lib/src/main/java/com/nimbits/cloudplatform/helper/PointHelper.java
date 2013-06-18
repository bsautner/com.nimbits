package com.nimbits.cloudplatform.helper;


import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.transaction.Transaction;

import java.util.List;


/**
 * Author: Benjamin Sautner
 * Date: 1/16/13
 * Time: 6:18 PM
 */
public class PointHelper {
//    public static DataPointHelper getInstance() {
//        return new DataPointHelper();
//    }

    public static Point createDataPoint(final String name, final String description)   {

        EntityName pointName = CommonFactory.createName(name, EntityType.point);

        Entity entity = EntityModelFactory.createEntity(pointName, description, EntityType.point, ProtectionLevel.everyone,
                Nimbits.session.getKey(),   Nimbits.session.getKey());

        Point point = PointModelFactory.createPointModel(entity, 0.0, 365, "", 0.0, false, false, false, 0, false, FilterType.none,
                0.0, false, PointType.basic, 0, false, 0.0);

        List<Point> sample;

            sample = Transaction.addEntity(point, PointModel.class);

        if (sample.isEmpty()) {
           if (pointExists(name)) {
               throw new IllegalStateException("Creating point failed - point already exists");
           }
           else {
           throw new IllegalStateException("Creating point failed");
           }
       }
       else {
           return sample.get(0);
       }


    }

    public static boolean pointExists(final String name)   {

        SimpleValue<String> id = SimpleValue.getInstance(Nimbits.session.getOwner() + "/" + name);
        List<Entity> sample;

            sample = Transaction.getEntity(id, EntityType.point, PointModel.class);

        return ! sample.isEmpty();



    }

    public static Point getPoint(final String name)   {

        SimpleValue<String> id = SimpleValue.getInstance(Nimbits.session.getOwner() + "/" + name);
        List<Point> sample;

            sample = Transaction.getEntity(id, EntityType.point, PointModel.class);

        if (sample.isEmpty()) {
            throw new IllegalStateException("Point Not Found");
        }
        else {
            return sample.get(0);
        }



    }


}
