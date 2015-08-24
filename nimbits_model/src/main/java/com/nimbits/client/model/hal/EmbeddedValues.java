
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;
import com.nimbits.client.model.value.Value;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class EmbeddedValues implements Serializable {


    @Expose
    private List<Value> children;

    public EmbeddedValues() {
    }

    public EmbeddedValues(List<Value> children) {
        this.children = children;
    }

    public List<Value> getChildren() {
        return children;
    }
}
