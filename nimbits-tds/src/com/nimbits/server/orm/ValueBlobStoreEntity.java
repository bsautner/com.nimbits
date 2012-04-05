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

package com.nimbits.server.orm;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.model.valueblobstore.*;

import javax.jdo.annotations.*;
import java.util.*;

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

    public ValueBlobStoreEntity(final String entity,
                                final Date timestamp,
                                final Date maxTimestamp,
                                final Date minTimestamp,
                                final String path,
                                final BlobKey blobkey,
                                final long length) {
        this.entity = entity;
        this.timestamp = timestamp.getTime();

        this.path = path;
        this.maxTimestamp = maxTimestamp.getTime();
        this.minTimestamp = minTimestamp.getTime();
        this.blobkey = blobkey;
        this.length = length;
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

    public String getBlobKey() {
        return blobkey.getKeyString();
    }

    public long getLength() {
        return length != null ? length : Const.CONST_DEFAULT_BLOB_LENGTH;
    }
}
