package com.nimbits.server.transaction.value;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.orm.ValueStore;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
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
        finally {
            pm.close();
        }



    }

    public List<Value> getSeries(Entity entity, Optional<Range<Date>> timespan, Optional<Range<Integer>> range, Optional<String> mask) {
        //TODO OOMA
        return null;
    }

    public void storeValues(Entity entity, List<Value> values) {
        //TODO OOMA
    }
}
