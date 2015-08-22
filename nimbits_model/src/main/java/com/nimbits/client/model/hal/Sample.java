
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import java.io.Serializable;

@Generated("org.jsonschema2pojo")
public class Sample  implements Serializable {

    @Expose
    private String href;
    @Expose
    private String description;



    public Sample(String href, String description) {
        this.href = href;
        this.description = description;
    }
}
