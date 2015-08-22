
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import java.io.Serializable;

@Generated("org.jsonschema2pojo")
public class Self implements Serializable {

    @Expose
    private String href;

    public Self(String href) {
        this.href = href;
    }

}
