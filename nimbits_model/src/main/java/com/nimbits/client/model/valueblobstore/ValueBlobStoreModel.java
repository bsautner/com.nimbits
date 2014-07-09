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

package com.nimbits.client.model.valueblobstore;



import java.io.Serializable;
import java.util.Date;

public class ValueBlobStoreModel implements Serializable, Comparable<ValueBlobStore>, ValueBlobStore {


    private String entity;

    private Date timestamp;

    private long maxTimestamp;

    private long minTimestamp;

    private long length;

  //  private String path;

    private String key;

    private Integer version;


    public ValueBlobStoreModel(ValueBlobStore store) {
        this.entity = store.getEntity();
        this.timestamp = store.getTimestamp();
       // this.path = store.getPath();
        this.maxTimestamp = store.getMaxTimestamp().getTime();
        this.minTimestamp = store.getMinTimestamp().getTime();
        this.key = store.getBlobKey();
        this.length = store.getLength();
        this.version = store.getVersion();
    }

    public ValueBlobStoreModel() {
    }

    @Override
    public String getEntity() {
        return entity;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }


//    @Override
//    public String getPath() {
//        return path;
//    }

    @Override
    public Date getMaxTimestamp() {
        return new Date(maxTimestamp);
    }

    @Override
    public Date getMinTimestamp() {
        return new Date(minTimestamp);
    }

    @Override
    public String getBlobKey() {
        return key;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public void validate()  {

    }
    @Override
    public Integer getVersion() {
        return version == null ? 0 : version;
    }

    @Override
    public int compareTo(ValueBlobStore that) {
        return this.timestamp.compareTo(that.getTimestamp());

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueBlobStoreModel that = (ValueBlobStoreModel) o;

        if (version != that.version) return false;
        if (length != that.length) return false;
        if (maxTimestamp != that.maxTimestamp) return false;
        if (minTimestamp != that.minTimestamp) return false;
        if (!entity.equals(that.entity)) return false;
        if (!key.equals(that.key)) return false;
       // if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (!timestamp.equals(that.timestamp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entity.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + (int) (maxTimestamp ^ (maxTimestamp >>> 32));
        result = 31 * result + (int) (minTimestamp ^ (minTimestamp >>> 32));
        result = 31 * result + (int) (length ^ (length >>> 32));
       // result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + key.hashCode();
        result = 31 * result + (version ^ (version >>> 32));
        return result;
    }
}
