
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class Embedded<T>  implements Serializable {

    @Expose
    private List<T> children = new ArrayList<T>();

    public Embedded(List<T> children) {
        this.children = children;
    }

    public List<T> getChildren() {
        return children;
    }
}
