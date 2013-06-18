/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.orm;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.constants.Const;
import com.nimbits.client.exception.NimbitsException;
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
    private String path;

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
                                final String path,
                                final BlobKey blobkey,
                                final long length,
                                final boolean compressed) {
        this.entity = entity;
        this.timestamp = timestamp.getTime();
        this.path = path;
        this.maxTimestamp = maxTimestamp.getTime();
        this.minTimestamp = minTimestamp.getTime();
        this.blobkey = blobkey;
        this.length = length;
        this.compressed = compressed;
    }


    @Override
    public String getEntity() {
        return entity;
    }

    @Override
    public Date getTimestamp() {
        return new Date(timestamp);
    }

    @Override
    public String getPath() {
        return path;
    }

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
        return blobkey.getKeyString();
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
    public void validate() throws NimbitsException {
        if (this.length == null || this.length == 0) {
            throw new NimbitsException("Store must not have a length of zero or null");
        }

        if (maxTimestamp < this.timestamp) {
            throw new NimbitsException("max timestamp was less than the base timestamp");
        }
        if (minTimestamp < this.timestamp) {
            throw new NimbitsException("min timestamp was less than the base timestamp");
        }
        if (this.maxTimestamp == 0 || this.minTimestamp == 0) {
            throw new NimbitsException("Min and Max timestamps should not be zero");
        }


    }

    @Override
    public int compareTo(ValueBlobStore that) {
        return new Date(this.timestamp).compareTo(that.getTimestamp());
    }
}