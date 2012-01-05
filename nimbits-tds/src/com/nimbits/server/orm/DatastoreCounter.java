package com.nimbits.server.orm;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/19/11
 * Time: 4:58 PM
 */
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents a counter in the datastore and stores the number of shards.
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DatastoreCounter {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String counterName;

    @Persistent
    private Integer numShards;

    public DatastoreCounter(final String counterName) {
        this.counterName = counterName;
        this.numShards = Integer.valueOf(0);
    }

    public DatastoreCounter(final String counterName, final Integer numShards) {
        this.counterName = counterName;
        this.numShards = numShards;
    }

    public Long getId() {
        return id;
    }

    public String getCounterName() {
        return counterName;
    }

    public Integer getShardCount() {
        return numShards;
    }

    public void setShardCount(final int count) {
        this.numShards = Integer.valueOf(count);
    }


}