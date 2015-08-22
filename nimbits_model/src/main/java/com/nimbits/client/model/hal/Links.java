
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import java.io.Serializable;

@Generated("org.jsonschema2pojo")
public class Links implements Serializable {

    @Expose
    private Self self;

    @Expose
    private Data data;

    @Expose
    private Parent parent;

    @Expose
    private Sample sample;

    public Links(Self self, Parent parent, Data data) {
        this.self = self;
        this.data = data;
        this.parent = parent;
    }

    public Links(Self self, Parent parent, Sample sample) {
        this.self = self;
        this.sample = sample;
        this.parent = parent;
    }



}
