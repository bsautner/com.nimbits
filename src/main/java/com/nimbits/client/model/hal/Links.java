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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Links implements Serializable {


    private Self self;


    private Series series;


    private Snapshot snapshot;


    private DataTable datatable;


    private Parent parent;


    private Sample sample;


    private Next next;


    private Children children;


    private Nearby nearby;

    public Links() {
    }

    public Links(Self self, Parent parent, Series series, Snapshot snapshot, DataTable datatable, Next next, Nearby nearby, Children children) {
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

    public Self getSelf() {
        return self;
    }

    public Series getSeries() {
        return series;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public DataTable getDatatable() {
        return datatable;
    }

    public Parent getParent() {
        return parent;
    }

    public Sample getSample() {
        return sample;
    }

    public Next getNext() {
        return next;
    }

    public Children getChildren() {
        return children;
    }

    public Nearby getNearby() {
        return nearby;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public void setDatatable(DataTable datatable) {
        this.datatable = datatable;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public void setNext(Next next) {
        this.next = next;
    }

    public void setChildren(Children children) {
        this.children = children;
    }

    public void setNearby(Nearby nearby) {
        this.nearby = nearby;
    }
}
