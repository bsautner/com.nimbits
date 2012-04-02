/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.orm;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;

import javax.jdo.annotations.*;
import java.util.Date;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 11:08 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ValueBlobStoreEntity  implements ValueBlobStore {

    private static final long serialVersionUID = 1L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String entity;

    @Persistent
    private long timestamp;

    @Persistent
    private long maxTimestamp;

    @Persistent
    private long minTimestamp;

    @Persistent
    private String path;

    @Persistent
    private BlobKey key;

    public ValueBlobStoreEntity(String entity, Date timestamp, Date maxTimestamp, Date minTimestamp, String path, BlobKey key) {
        this.entity = entity;
        this.timestamp = timestamp.getTime();

        this.path = path;
        this.maxTimestamp = maxTimestamp.getTime();
        this.minTimestamp = minTimestamp.getTime();
        this.key = key;
    }


    public ValueBlobStoreEntity() {
    }


    public String getEntity() {
        return entity;
    }

    public Date getTimestamp() {
        return new Date(timestamp);
    }

    public String getPath() {
        return path;
    }

    public Date getMaxTimestamp() {
        return new Date(maxTimestamp);
    }

    public void setMaxTimestamp(Date maxTimestamp) {
        this.maxTimestamp = maxTimestamp.getTime();
    }

    public Date getMinTimestamp() {
        return new Date(minTimestamp);
    }

    public void setMinTimestamp(Date minTimestamp) {
        this.minTimestamp = minTimestamp.getTime();
    }

    public String getKey() {
        return key.getKeyString();
    }


}
