
/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import java.io.Serializable;

@Generated("org.jsonschema2pojo")
public class Links implements Serializable {

    @Expose
    private Self self;

    @Expose
    private Series series;

    @Expose
    private Snapshot snapshot;

    @Expose
    private DataTable datatable;

    @Expose
    private Parent parent;

    @Expose
    private Sample sample;

    @Expose
    private Next next;

    @Expose
    private Children children;

    @Expose
    private Nearby nearby;

    public Links() {
    }

    public Links(Self self, Parent parent, Series series, Snapshot snapshot, DataTable datatable, Next next, Nearby nearby, Children children ) {
        this.self = self;
        this.series = series;
        this.snapshot = snapshot;
        this.datatable = datatable;
        this.parent = parent;
        this.next = next;
        this.nearby = nearby;
        this.children = children;
    }

    public Links(Self self, Parent parent, Sample sample) {
        this.self = self;
        this.sample = sample;
        this.parent = parent;
    }



}
