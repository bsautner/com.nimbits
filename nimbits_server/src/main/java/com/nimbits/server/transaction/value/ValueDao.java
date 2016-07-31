package com.nimbits.server.transaction.value;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.orm.ValueStore;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class ValueDao {


    private PersistenceManagerFactory persistenceManagerFactory;


    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;

    }

    public Value getSnapshot(Entity entity) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query<ValueStore> q1;

            q1 = pm.newQuery(ValueStore.class);


            q1.setFilter("entityId==i");
            q1.declareParameters("String i");
            q1.orderBy("timestamp desc");
            q1.setRange(0, 1);


            final List<ValueStore> result = (List<ValueStore>) q1.execute(entity.getId());
            if (result.isEmpty()) {
                return new Value.Builder().create();
            }
            else {
                return new Value.Builder().initValue(result.get(0).getValue()).create();
            }



        } finally {
            pm.close();
        }
    }

    public void setSnapshot(Entity entity, Value value)  {


        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            ValueStore valueStore = new ValueStore(entity.getId(), value);

            pm.makePersistent(valueStore);

        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        finally {
            pm.close();
        }



    }

    public void deleteAllData(Point entity) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query<ValueStore> q1;

            q1 = pm.newQuery(ValueStore.class);


            q1.setFilter("entityId==i");
            q1.declareParameters("String i");


            final List<ValueStore> result = (List<ValueStore>) q1.execute(entity.getId());
            pm.deletePersistentAll(result);



        } finally {
            pm.close();
        }
    }

    public void storeValues(Entity entity, List<Value> values) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            List<ValueStore> stores = new ArrayList<>(values.size());
            for (Value value : values) {
                stores.add(new ValueStore(entity.getId(), value));

            }

            pm.makePersistentAll(stores);

        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        finally {
            pm.close();
        }
    }

    public List<Value> getSeries(Entity entity, Optional<Range<Long>> timespan, Optional<Range<Integer>> range, Optional<String> mask) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query<ValueStore> q1;

            q1 = pm.newQuery(ValueStore.class);
            final List<ValueStore> result;

            if (timespan.isPresent() && ! range.isPresent()) {
                q1.setFilter("entityId==i && timestamp >= s && timestamp <= e");
                q1.declareParameters("String i, Long s, Long e");
                q1.orderBy("timestamp desc");
                result = (List<ValueStore>) q1.execute(entity.getId(), timespan.get().lowerEndpoint(), timespan.get().upperEndpoint());
            } else if (! timespan.isPresent() && range.isPresent()) {
                q1.setFilter("entityId==i");
                q1.declareParameters("String i");
                q1.orderBy("timestamp desc");
                q1.setRange(range.get().lowerEndpoint(), range.get().upperEndpoint());
                result = (List<ValueStore>) q1.execute(entity.getId());

            } else if (timespan.isPresent() && range.isPresent()) {
                q1.setFilter("entityId==i && timestamp >= s && timestamp <= e");
                q1.declareParameters("String i, Long s, Long e");
                q1.orderBy("timestamp desc");
                q1.setRange(range.get().lowerEndpoint(), range.get().upperEndpoint());
                result = (List<ValueStore>) q1.execute(entity.getId(), timespan.get().lowerEndpoint(), timespan.get().upperEndpoint());

            } else {
                q1.setFilter("entityId==i");
                q1.declareParameters("String i");
                q1.orderBy("timestamp desc");
                result = (List<ValueStore>) q1.execute(entity.getId());
            }






            List<Value> retList = new ArrayList<>(result.size());
            for (ValueStore store : result) {
                retList.add(new Value.Builder().initValue(store.getValue()).create());
            }
            return ImmutableList.copyOf(retList);



        } finally {
            pm.close();
        }
    }
}
