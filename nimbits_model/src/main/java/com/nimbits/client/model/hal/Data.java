
package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import java.io.Serializable;

@Generated("org.jsonschema2pojo")
public class Data implements Serializable {

    @Expose
    private String href;

    public Data() {
    }

    public Data(String href) {
        this.href = href;
    }

    /**
     * 
     * @return
     *     The href
     */
    public String getHref() {
        return href;
    }

    /**
     * 
     * @param href
     *     The href
     */
    public void setHref(String href) {
        this.href = href;
    }

}
