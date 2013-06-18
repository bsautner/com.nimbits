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

package com.nimbits.cloudplatform.client.model.valueblobstore;



import java.io.Serializable;
import java.util.Date;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/23/12
 * Time: 10:47 AM
 */
public class ValueBlobStoreModel implements Serializable, Comparable<ValueBlobStore>, ValueBlobStore {


    private String entity;

    private Date timestamp;

    private long maxTimestamp;

    private long minTimestamp;

    private long length;

  //  private String path;

    private String key;

    private boolean compressed;


    public ValueBlobStoreModel(ValueBlobStore store) {
        this.entity = store.getEntity();
        this.timestamp = store.getTimestamp();
       // this.path = store.getPath();
        this.maxTimestamp = store.getMaxTimestamp().getTime();
        this.minTimestamp = store.getMinTimestamp().getTime();
        this.key = store.getBlobKey();
        this.length = store.getLength();
        this.compressed = store.getCompressed();
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
    public Boolean getCompressed() {
        return this.compressed;
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

        if (compressed != that.compressed) return false;
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
        result = 31 * result + (compressed ? 1 : 0);
        return result;
    }
}
