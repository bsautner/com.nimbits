package com.nimbits.server.orm;

import com.google.appengine.api.blobstore.*;

import javax.jdo.annotations.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 11:08 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ValueBlobStoreEntity {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String entity;

    @Persistent
    private Date timestamp;

    @Persistent
    private Date maxTimestamp;

    @Persistent
    private Date minTimestamp;

    @Persistent
    private String path;

    @Persistent
    BlobKey blobKey;

    public ValueBlobStoreEntity(String entity, Date timestamp, Date maxTimestamp, Date minTimestamp, BlobKey blobKey, String path) {
        this.entity = entity;
        this.timestamp = timestamp;
        this.blobKey = blobKey;
        this.path = path;
        this.maxTimestamp = maxTimestamp;
        this.minTimestamp = minTimestamp;
    }

    public String getEntity() {
        return entity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public BlobKey getBlobKey() {
        return blobKey;
    }

    public String getPath() {
        return path;
    }

    public Date getMaxTimestamp() {
        return maxTimestamp;
    }

    public void setMaxTimestamp(Date maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    public Date getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestamp(Date minTimestamp) {
        this.minTimestamp = minTimestamp;
    }
}
