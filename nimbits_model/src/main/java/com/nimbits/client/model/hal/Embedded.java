
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class Embedded implements Serializable {


    @Expose
    private List<EntityChild> children;

    public Embedded() {
    }

    public Embedded(List<EntityChild> children) {
        this.children = children;
    }

    public List<EntityChild> getChildren() {
        return children;
    }
}
