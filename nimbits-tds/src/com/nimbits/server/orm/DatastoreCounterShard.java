package com.nimbits.server.orm;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/19/11
 * Time: 4:59 PM
 */
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * One shard belonging to the named counter.
 *
 * An individual shard is written to infrequently to allow the counter in
 * aggregate to be incremented rapidly.
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DatastoreCounterShard {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Integer shardNumber;

    @Persistent
    private String counterName;

    @Persistent
    private Integer count;

    public DatastoreCounterShard(String counterName, int shardNumber) {
        this(counterName, shardNumber, 0);
    }

    public DatastoreCounterShard(String counterName, int shardNumber,
                                 int count) {
        this.counterName = counterName;
        this.shardNumber = Integer.valueOf(shardNumber);
        this.count = Integer.valueOf(count);
    }

    public Long getId() {
        return id;
    }

    public String getCounterName() {
        return counterName;
    }

    public Integer getShardNumber() {
        return shardNumber;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void increment(int amount) {
        count = Integer.valueOf(count.intValue() + amount);
    }
}
