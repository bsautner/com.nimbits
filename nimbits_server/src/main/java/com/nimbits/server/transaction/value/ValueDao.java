package com.nimbits.server.transaction.value;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.orm.ValueStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Repository
public class ValueDao {


    private PersistenceManagerFactory persistenceManagerFactory;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ValueDao.class);

    @Autowired
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;

    }

    @Cacheable(cacheNames = "snapshots", key="#entity.id")
    public Value getSnapshot(Entity entity) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        logger.info("getting snapshot");
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
            } else {
                return new Value.Builder().initValue(result.get(0).getValue()).create();
            }


        } finally {
            pm.close();
        }
    }

    @CacheEvict(cacheNames = "snapshots", key="#entity.id")
    public void setSnapshot(Entity entity, Value value) {


        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        logger.info("storing snapshot");
        try {
            ValueStore valueStore = new ValueStore(entity.getId(), value);

            pm.makePersistent(valueStore);

        } finally {
            pm.close();
        }


    }

    public List<Value> getSeries(Entity entity, Optional<Range<Long>> timespan, Optional<Range<Integer>> range, Optional<String> mask) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query<ValueStore> q1 = pm.newQuery(ValueStore.class);
            final List<ValueStore> result;
            final List<Value> retList = new ArrayList<>();

            if (timespan.isPresent() && !range.isPresent()) {
                q1.setFilter("entityId==i && timestamp >= sd && timestamp <= ed");
                q1.declareParameters("String i, Long sd, Long ed");
                q1.orderBy("timestamp desc");
                result = (List<ValueStore>) q1.execute(entity.getId(), timespan.get().lowerEndpoint(), timespan.get().upperEndpoint());
            } else if (!timespan.isPresent() && range.isPresent()) {
                q1.setFilter("entityId==i");
                q1.declareParameters("String i");
                q1.orderBy("timestamp desc");
                q1.setRange(range.get().lowerEndpoint(), range.get().upperEndpoint());
                result = (List<ValueStore>) q1.execute(entity.getId());
            } else if (timespan.isPresent() && range.isPresent()) {
                q1.setFilter("entityId==i && timestamp >= sd && timestamp <= ed");
                q1.declareParameters("String i, Long sd, Long ed");
                q1.orderBy("timestamp desc");
                q1.setRange(range.get().lowerEndpoint(), range.get().upperEndpoint());
                result = (List<ValueStore>) q1.execute(entity.getId(), timespan.get().lowerEndpoint(), timespan.get().upperEndpoint());

            } else {
                q1.setFilter("entityId==i");
                q1.declareParameters("String i");
                q1.orderBy("timestamp desc");
                result = (List<ValueStore>) q1.execute(entity.getId(), timespan.get().lowerEndpoint(), timespan.get().upperEndpoint());
            }

            if (result != null) {
                if (mask.isPresent()) {
                    String m = mask.get();
                    for (ValueStore valueStore : result) {
                        if (containsMask(valueStore.getValue().getMetaData(), m)) {
                            retList.add(valueStore.getValue());
                        }
                    }
                } else {
                    for (ValueStore valueStore : result) {
                        retList.add(valueStore.getValue());
                    }
                }


                return ImmutableList.copyOf(retList);
            } else {
                return Collections.emptyList();
            }

        } finally {
            pm.close();
        }
    }

    @CacheEvict(cacheNames = "snapshots", key="#entity.id")
    public void storeValues(Entity entity, List<Value> values) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            List<ValueStore> stores = new ArrayList<>(values.size());
            for (Value value : values) {
                stores.add(new ValueStore(entity.getId(), value));
            }
            pm.makePersistentAll(stores);
        } finally {
            pm.close();
        }
    }

    @CacheEvict(cacheNames = "snapshots", key="#entity.id")
    public void deleteAllData(Entity entity) {
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


    private boolean containsMask(String metadata, String mask) {

        if (!StringUtils.isEmpty(mask) && mask.equals(metadata)) {

            return true;
        } else if (!StringUtils.isEmpty(mask) && !StringUtils.isEmpty(metadata)) {
            try {
                Pattern p = Pattern.compile(mask);

                Matcher m = p.matcher(metadata);
                return m.find();
            } catch (PatternSyntaxException ex) {

                return false;
            }


        }

        return false;

    }

}
