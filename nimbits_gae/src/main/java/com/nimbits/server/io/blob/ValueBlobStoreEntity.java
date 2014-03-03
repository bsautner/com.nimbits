/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.io.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.nimbits.client.constants.Const;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;

import javax.jdo.annotations.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 11:08 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ValueBlobStoreEntity implements ValueBlobStore {
    static final Logger log = Logger.getLogger(ValueBlobStoreEntity.class.getName());


    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key key;

    @Persistent
    private long timestamp;

    @Persistent
    private long maxTimestamp;

    @Persistent
    private long minTimestamp;

    @Persistent
    private String entity;

    @Persistent
    private Long length;

    @Persistent
    private BlobKey blobkey;

    @Persistent
    private Boolean compressed;


    public ValueBlobStoreEntity(final String entity,
                                final Date timestamp,
                                final Date maxTimestamp,
                                final Date minTimestamp,
                                final BlobKey blobkey,
                                final long length,
                                final boolean compressed) {
        this.entity = entity;
        this.timestamp = timestamp.getTime();
        this.maxTimestamp = maxTimestamp.getTime();
        this.minTimestamp = minTimestamp.getTime();
        this.blobkey = blobkey;
        this.length = length;
        this.compressed = compressed;
    }

    public Key getKey() {
        return key;
    }

    @Override
    public String getEntity() {
        return entity;
    }

    @Override
    public Date getTimestamp() {
        return new Date(timestamp);
    }

//    @Override
//    public String getPath() {
//        return path.getValue();
//    }

    @Override
    public Date getMaxTimestamp() {
        return new Date(maxTimestamp);
    }

    @Override
    public void setMaxTimestamp(Date maxTimestamp) {
        this.maxTimestamp = maxTimestamp.getTime();
    }

    @Override
    public Date getMinTimestamp() {
        return new Date(minTimestamp);
    }

    @Override
    public void setMinTimestamp(Date minTimestamp) {
        this.minTimestamp = minTimestamp.getTime();
    }

    @Override
    public String getBlobKey() {
        return blobkey == null ? "" : blobkey.getKeyString();
    }

    @Override
    public long getLength() {
        return length != null ? length : Const.CONST_DEFAULT_BLOB_LENGTH;
    }

    @Override
    public Boolean getCompressed() {
        return compressed == null ? false : compressed;
    }

    @Override
    public void validate() {
        if (this.length == null || this.length == 0) {
            log.severe("blobstore validation error");
            throw new IllegalArgumentException("Store must not have a length of zero or null");
        }

        if (maxTimestamp < this.timestamp) {
            log.severe("blobstore validation error");
            throw new IllegalArgumentException("max timestamp was less than the base timestamp");
        }
        if (minTimestamp < this.timestamp) {
            log.severe("blobstore validation error");
            throw new IllegalArgumentException("min timestamp was less than the base timestamp");
        }
        if (this.maxTimestamp == 0 || this.minTimestamp == 0) {
            log.severe("blobstore validation error");
            throw new IllegalArgumentException("Min and Max timestamps should not be zero");
        }


    }

    @Override
    public int compareTo(ValueBlobStore that) {
        return new Date(this.timestamp).compareTo(that.getTimestamp());
    }


}