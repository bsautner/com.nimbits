
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.io.Serializable;

@Generated("org.jsonschema2pojo")
public class EntityChild  implements Serializable {

    @SerializedName("_links")
    @Expose
    private Links links;

    @Expose
    private String name;

    public EntityChild() {
    }

    public EntityChild(Links links, String name) {
        this.links = links;
        this.name = name;
    }






}
