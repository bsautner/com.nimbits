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

package com.nimbits.client.model.valueblobstore;

import java.util.Date;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/23/12
 * Time: 10:47 AM
 */
public class ValueBlobStoreModel implements ValueBlobStore {


    private String entity;


    private Date timestamp;


    private long maxTimestamp;


    private long minTimestamp;


    private String path;

    private String key;

    public ValueBlobStoreModel(String entity, Date timestamp, Date maxTimestamp, Date minTimestamp, String path, String key) {
        this.entity = entity;
        this.timestamp = timestamp;
        this.path = path;
        this.maxTimestamp = maxTimestamp.getTime();
        this.minTimestamp = minTimestamp.getTime();
        this.key = key;
    }

    public ValueBlobStoreModel(ValueBlobStore store) {
        this.entity = store.getEntity();
        this.timestamp = store.getTimestamp();
        this.path = store.getPath();
        this.maxTimestamp = store.getMaxTimestamp().getTime();
        this.minTimestamp = store.getMinTimestamp().getTime();
        this.key = store.getKey();
    }

    public String getEntity() {
        return entity;
    }

    public Date getTimestamp() {
        return timestamp;
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

    @Override
    public String getKey() {
        return key;
    }
}
